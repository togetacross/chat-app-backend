package com.mycompany.chatappbackend.service;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mycompany.chatappbackend.exception.ResourceNotFoundException;
import com.mycompany.chatappbackend.model.dto.Message.AttachmentDTO;
import com.mycompany.chatappbackend.model.dto.Message.DisplayMessageDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageSliceDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageUserActivityDTO;
import com.mycompany.chatappbackend.model.entity.Attachment;
import com.mycompany.chatappbackend.model.entity.AttachmentType;
import com.mycompany.chatappbackend.model.entity.ChatRoomUser;
import com.mycompany.chatappbackend.model.entity.Message;
import com.mycompany.chatappbackend.model.entity.MessageContent;
import com.mycompany.chatappbackend.model.entity.MessageType;
import com.mycompany.chatappbackend.model.entity.MessageUserActivity;
import com.mycompany.chatappbackend.repository.MessageRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MessageService {

	private static final Integer MESSAGE_PAGE_SIZE = 4;
	private static final Integer INIT_PAGE = 0;
	
	@Value("${files_storage.dir}")
	private String filesStorage;

	@Autowired
	private MessageRepository messageRepository;
	
	@Autowired
	private ChatRoomUserService chatRoomUserService;
	
	@Autowired
	private FileService fileService;
	
	public Message getMessageById(Integer id) {
		return messageRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Message not found!"));
	}
	
	public List<Message> getMessagesByMessageIds(List<Integer> messageIds) {
		return (List<Message>) messageRepository.findAllById(messageIds);
	}
	
	public DisplayMessageDTO saveUserActivityMessage(Integer userId, Integer chatroomId, MessageType messageType) {
		ChatRoomUser chatroomUser = chatRoomUserService.getReferenceById(userId, chatroomId);
		Message message = new Message(messageType, chatroomUser);
		message.addUserActivity(new MessageUserActivity(chatroomUser.getUser(), OffsetDateTime.now(ZoneId.systemDefault())));
		Message savedMessage = messageRepository.save(message);
		return createDisplayMessageDTO(savedMessage);
	}
	
	//@Transactional
	public DisplayMessageDTO saveMessage(MultipartFile[] files, MessageDTO messageDTO, Integer userId) {
		ChatRoomUser chatRoomUser = chatRoomUserService.getChatRoomUserById(userId, messageDTO.getChatRoomId()).orElseThrow();
		Message message = new Message();
		message.setCreatedAt(messageDTO.getDateTime());
		message.setChatRoomUser(chatRoomUser);
		message.setMessageType(MessageType.MESSAGE);
		message.addUserActivity(new MessageUserActivity(chatRoomUser.getUser(), messageDTO.getDateTime()));

		MessageContent messageContent = new MessageContent();
		messageContent.setContent(messageDTO.getContent());
		
		if (files != null) {
			Arrays.stream(files).forEach(file -> {
				try {
					AttachmentType attachmentType = fileService.getFileType(file.getOriginalFilename());
					String fileName = messageDTO.getDateTime().toInstant().toEpochMilli() + "-" +  file.getOriginalFilename();
					fileService.updload(file, messageDTO.getChatRoomId().toString(), fileName);		
					Attachment attachment = new Attachment(attachmentType, fileName);
					messageContent.addAttachment(attachment);
				} catch (IOException e) {
					log.warn(e.getMessage());
				}
			});	
		}	
		message.setMessageContent(messageContent);
		Message savedMessage = messageRepository.save(message);
		return createDisplayMessageDTO(savedMessage);
	}

	public MessageSliceDTO getPaginateMessages(Integer chatRoomId, OffsetDateTime offsetDateTime) {
		Pageable paging = PageRequest.of(INIT_PAGE, MESSAGE_PAGE_SIZE);
		Slice<Integer> ids;
		if(offsetDateTime == null) {
			ids = messageRepository.findTop10IdByChatRoomIdOrderByCreatedAtDesc(chatRoomId, paging);				
		} else {			
			ids = messageRepository.findByChatRoomIdByCreatedAtBefore(chatRoomId, offsetDateTime, paging);
		}
		List<Message> messages = messageRepository.findByIdInOrderByCreatedAtdDesc(ids.toList());
		List<DisplayMessageDTO> messagesDTO = convertMessageListToMessageDTOList(messages);
		return new MessageSliceDTO(messagesDTO, ids.hasNext());
	}

	private List<DisplayMessageDTO> convertMessageListToMessageDTOList(List<Message> messageList) {
		return messageList.stream()
			.map(message -> createDisplayMessageDTO(message))
			.sorted(Comparator.comparing(DisplayMessageDTO::getCreatedAt))
			.collect(Collectors.toList());
	}
	
	private DisplayMessageDTO createDisplayMessageDTO(Message message) {
		return DisplayMessageDTO.builder()
				.id(message.getId())
				.userId(message.getChatRoomUser().getKey().getUserId())
				.roomId(message.getChatRoomUser().getKey().getChatRoomId())
				.type(message.getMessageType().name())
				.createdAt(message.getCreatedAt())
				.content(message.getMessageContent() != null ? message.getMessageContent().getContent() : "")
				.files(message.getMessageContent() != null ? message.getMessageContent().getAttachments() 
						.stream()
						 .map(attachment -> {
							byte[] file = null;
							if(attachment.getAttachmentType().equals(AttachmentType.IMAGE)) {			
								file = fileService.getFileInByteArray(attachment.getName(), message.getChatRoomUser().getKey().getChatRoomId().toString());
							} 
							return new AttachmentDTO(attachment.getId(), attachment.getName(), file, attachment.getAttachmentType());
						})
						.collect(Collectors.toSet()) : new ArrayList<>())
				.userActivitys(message.getMessageUserActivitys() != null ? message.getMessageUserActivitys()
						.stream()
							.map(activity -> {
								return new MessageUserActivityDTO(activity.getUser().getId(), activity.getSeenAt(), activity.isLiked());
							})
						.collect(Collectors.toSet()) : new HashSet<>())
				.build();
	}

}
