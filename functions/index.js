const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const logger = require("firebase-functions/logger");
const admin = require("firebase-admin");
admin.initializeApp();

/**
 * 🔥 Trigger: Khi ai đó LIKE bài viết
 * Collection: posts/{postId}/likes/{userId}
 */
exports.onPostLiked = onDocumentCreated(
  "posts/{postId}/likes/{userId}",
  async (event) => {
    const { postId, userId } = event.params;

    try {
      // Lấy thông tin người like
      const liker = await admin.firestore().collection("users").doc(userId).get();
      const likerName = liker.data()?.displayName || "Ai đó";

      // Lấy thông tin bài viết
      const post = await admin.firestore().collection("posts").doc(postId).get();
      const postOwnerId = post.data()?.authorId;

      // Lấy token chủ bài
      const owner = await admin.firestore().collection("users").doc(postOwnerId).get();
      const fcmToken = owner.data()?.fcmToken;

      if (!fcmToken) return;

      const message = {
        token: fcmToken,
        notification: {
          title: "Có người thích bài viết của bạn ❤️",
          body: `${likerName} đã thích bài viết của bạn.`,
        },
        data: {
          postId: postId,
        },
      };

      await admin.messaging().send(message);
      logger.info("📢 Sent like notification!");
    } catch (e) {
      logger.error("❌ Error sending like notification:", e);
    }
  }
);

/**
 * 🔥 Trigger: Khi ai đó COMMENT bài viết
 * Collection: posts/{postId}/comments/{commentId}
 */
exports.onPostCommented = onDocumentCreated(
  "posts/{postId}/comments/{commentId}",
  async (event) => {
    const { postId, commentId } = event.params;

    try {
      const commentData = event.data.data();
      const commenterId = commentData.authorId;   // ✔ đúng field!

      // Lấy thông tin người comment
      const commenter = await admin.firestore().collection("users").doc(commenterId).get();
      const commenterName = commenter.data()?.displayName || "Ai đó";

      // Lấy thông tin bài viết
      const post = await admin.firestore().collection("posts").doc(postId).get();
      const postOwnerId = post.data()?.authorId;

      // Không gửi nếu chủ bài tự comment
      if (postOwnerId === commenterId) return;

      // Lấy token chủ bài
      const owner = await admin.firestore().collection("users").doc(postOwnerId).get();
      const fcmToken = owner.data()?.fcmToken;

      if (!fcmToken) return;

      const message = {
        token: fcmToken,
        notification: {
          title: "Bài viết của bạn có bình luận mới 💬",
          body: `${commenterName}: ${commentData.text}`,
        },
        data: {
          postId: postId,
        },
      };

      await admin.messaging().send(message);
      logger.info("📢 Sent comment notification!");
    } catch (e) {
      logger.error("❌ Error sending comment notification:", e);
    }
  }
);
