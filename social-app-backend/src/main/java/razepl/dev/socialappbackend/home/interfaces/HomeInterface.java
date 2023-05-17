package razepl.dev.socialappbackend.home.interfaces;

import org.springframework.http.ResponseEntity;
import razepl.dev.socialappbackend.entities.user.User;
import razepl.dev.socialappbackend.home.data.*;

import java.util.List;

public interface HomeInterface {
    ResponseEntity<UserData> getUserData(User user);

    ResponseEntity<List<FriendData>> getFriendsList(User user);

    ResponseEntity<List<PostData>> getPostsList(int offsetValue, User user);

    ResponseEntity<PostData> createPost(String postContent, User user);

    ResponseEntity<LikeResponse> changePostNumberOfLikes(long postId, User user);

    ResponseEntity<Void> deletePost(long postId);
}
