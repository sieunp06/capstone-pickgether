package com.capstone.pick.service;

import com.capstone.pick.domain.Follow;
import com.capstone.pick.dto.FollowDto;
import com.capstone.pick.repository.FollowRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@DisplayName("팔로우")
@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @InjectMocks
    private FollowService followService;
    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    @DisplayName("유저 아이디를 넘겨주면, 해당 유저의 팔로워 리스트를 받아올 수 있다.")
    void findFollowerList() {
        //given
        User fromUser1 = User.builder().userId("fromUser1").build();
        User fromUser2 = User.builder().userId("fromUser2").build();
        User user = User.builder().userId("user").build();
        Follow follow1 = createFollow(1L, fromUser1, user);
        Follow follow2 = createFollow(2L, fromUser2, user);

        List<Follow> followerList = new ArrayList<>();
        followerList.add(follow1);
        followerList.add(follow2);

        given(followRepository.findAllByToUser("user")).willReturn(followerList);

        //when
        List<FollowDto> followerDtos = followService.findFollowerList("user");

        //then
        assertThat(followerDtos.get(0))
                .hasFieldOrPropertyWithValue("id", follow1.getId());
        assertThat(followerDtos.size()).isEqualTo(2);
    }

    @Test
    @DisplayName("유저 아이디를 넘겨주면, 해당 유저의 팔로잉 리스트를 받아올 수 있다.")
    void findFollowingList() {
        //given
        User toUser1 = User.builder().userId("toUser1").build();
        User toUser2 = User.builder().userId("toUser2").build();
        User user = User.builder().userId("user").build();
        Follow follow1 = createFollow(1L, user, toUser1);
        Follow follow2 = createFollow(2L, user, toUser2);

        List<Follow> followerList = new ArrayList<>();
        followerList.add(follow1);
        followerList.add(follow2);

        given(followRepository.findAllByToUser("user")).willReturn(followerList);

        //when
        List<FollowDto> followerDtos = followService.findFollowerList("user");

        //then
        assertThat(followerDtos.get(0))
                .hasFieldOrPropertyWithValue("id", follow1.getId());
        assertThat(followerDtos.size()).isEqualTo(2);
    }

    private static Follow createFollow(Long id, User fromUser, User toUser) {
        return Follow.builder()
                .id(id)
                .fromUser(fromUser)
                .toUser(toUser)
                .build();
    }

}