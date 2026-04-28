package com.example.MergeX.repository;

import com.example.MergeX.model.Friendship;
import com.example.MergeX.model.FriendshipStatus;
import com.example.MergeX.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    List<Friendship> findByUserAndStatus(User user, FriendshipStatus status);
    List<Friendship> findByFriendAndStatus(User friend, FriendshipStatus status);
    Optional<Friendship> findByUserAndFriend(User user, User friend);
}
