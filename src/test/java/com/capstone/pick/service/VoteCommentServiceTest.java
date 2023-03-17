package com.capstone.pick.service;

import com.capstone.pick.domain.User;
import com.capstone.pick.domain.Vote;
import com.capstone.pick.domain.VoteComment;
import com.capstone.pick.domain.constant.Category;
import com.capstone.pick.domain.constant.DisplayRange;
import com.capstone.pick.dto.CommentDto;
import com.capstone.pick.dto.UserDto;
import com.capstone.pick.exeption.UserMismatchException;
import com.capstone.pick.repository.CommentLikeRepository;
import com.capstone.pick.repository.UserRepository;
import com.capstone.pick.repository.VoteCommentRepository;
import com.capstone.pick.repository.VoteRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 서비스 로직 - 투표 댓글")
@ExtendWith(MockitoExtension.class)
public class VoteCommentServiceTest {

    @InjectMocks
    private VoteCommentService voteCommentService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private VoteRepository voteRepository;
    @Mock
    private VoteCommentRepository voteCommentRepository;
    @Mock
    private CommentLikeRepository commentLikeRepository;

    @DisplayName("댓글 상세 보기 페이지를 조회하면, 해당 투표 게시글에 대한 투표 댓글을 반환한다.")
    @Test
    void readComment() {
        // given
        User user1 = createUser("user1", "nick1");
        User user2 = createUser("user2", "nick2");
        Vote vote = createVote(1L, user1);

        VoteComment voteComment1 = createVoteComment(1L, user1, vote, "content");
        VoteComment voteComment2 = createVoteComment(2L, user2, vote, "content");

        List<VoteComment> voteComments = List.of(voteComment1, voteComment2);
        given(voteCommentRepository.getVoteCommentsByVoteId(anyLong())).willReturn(voteComments);

        // when
        List<CommentDto> commentDtos = voteCommentService.readComment(vote.getId());

        // then
        assertThat(commentDtos.size()).isEqualTo(2);
        then(voteCommentRepository).should().getVoteCommentsByVoteId(anyLong());
    }

    @DisplayName("투표 댓글을 입력하면, 투표 댓글을 저장한다.")
    @Test
    void saveComment() {
        // given
        User user1 = createUser("user1", "nick1");
        Vote vote = createVote(1L, user1);
        CommentDto commentDto = createCommentDto(vote.getId(), UserDto.from(user1), "content1");

        given(userRepository.getReferenceById(anyString())).willReturn(user1);
        given(voteRepository.getReferenceById(anyLong())).willReturn(vote);

        // when
        voteCommentService.saveComment(commentDto);

        // then
        then(userRepository).should().getReferenceById(anyString());
        then(voteRepository).should().getReferenceById(anyLong());
        then(voteCommentRepository).should().save(any(VoteComment.class));
    }

    @DisplayName("댓글 수정 정보를 입력하면, 투표 댓글을 수정한다.")
    @Test
    void updateComment() throws UserMismatchException {
        // given
        User user1 = createUser("user1", "nick1");
        Vote vote = createVote(1L, user1);
        VoteComment voteComment = createVoteComment(1L, user1, vote, "content1");

        CommentDto commentDto = createCommentDto(vote.getId(), UserDto.from(user1), "content2");

        given(voteCommentRepository.getReferenceById(anyLong())).willReturn(voteComment);
        given(userRepository.getReferenceById(anyString())).willReturn(user1);

        // when
        voteCommentService.updateComment(voteComment.getId(), commentDto);

        // then
        assertThat(voteComment).hasFieldOrPropertyWithValue("content", commentDto.getContent());
        then(voteCommentRepository).should().getReferenceById(anyLong());
        then(userRepository).should().getReferenceById(anyString());
    }

    @DisplayName("댓글 id를 입력하면, 투표 댓글을 삭제한다.")
    @Test
    void deleteComment() throws UserMismatchException {
        // given
        User user1 = createUser("user1", "nick1");
        Vote vote = createVote(1L, user1);
        VoteComment voteComment = createVoteComment(1L, user1, vote, "content1");

        given(userRepository.getReferenceById(anyString())).willReturn(user1);
        given(voteCommentRepository.getReferenceById(anyLong())).willReturn(voteComment);

        // when
        voteCommentService.deleteComment(voteComment.getId(), user1.getUserId());

        // then
        then(voteCommentRepository).should().delete(any(VoteComment.class));
    }

    private static User createUser(String userId, String nickname) {
        return User.builder()
                .userId(userId)
                .userPassword("password")
                .email("email@email.com")
                .nickname(nickname)
                .memo("memo")
                .birthday(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();
    }

    private static Vote createVote(Long id, User user) {
        Vote vote = Vote.builder()
                .user(user)
                .title("title")
                .content("content")
                .category(Category.FREE)
                .createAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusDays(3))
                .isMultiPick(true)
                .displayRange(DisplayRange.PUBLIC)
                .build();
        ReflectionTestUtils.setField(vote, "id", id);
        return vote;
    }

    private static VoteComment createVoteComment(Long id, User user, Vote vote, String content) {
        VoteComment voteComment = VoteComment.builder()
                .user(user)
                .vote(vote)
                .createAt(LocalDateTime.now())
                .content(content)
                .build();
        ReflectionTestUtils.setField(voteComment, "id", id);
        return voteComment;
    }

    private static CommentDto createCommentDto(Long voteId, UserDto userDto, String content) {
        return CommentDto.builder()
                .voteId(voteId)
                .userDto(userDto)
                .content(content)
                .createAt(LocalDateTime.now())
                .build();
    }
}
