import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const ALLOWED_DOMAIN = "@ut.edu.vn";

/**
 * Chạy khi Auth tạo user mới.
 * - Sai domain -> disable user (hoặc delete).
 * - Đúng domain -> set emailDomain vào Firestore.
 */
export const enforceUthDomain = functions.auth.user().onCreate(async (user) => {
  const email = user.email ?? "";
  const uid = user.uid;

  const auth = admin.auth();
  const db = admin.firestore();

  if (!email || !email.endsWith(ALLOWED_DOMAIN)) {
    try { await auth.updateUser(uid, { disabled: true }); } catch (e) { console.error(e); }
    try { await db.collection("users").doc(uid).delete(); } catch { /* ignore */ }
    console.log(`Blocked non-UTH account: ${email} (${uid})`);
    return;
  }

  try {
    await db.collection("users").doc(uid).set(
      {
        emailDomain: "ut.edu.vn",
        emailVerified: !!user.emailVerified,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
      },
      { merge: true }
    );
  } catch (e) {
    console.error("Set profile fail:", e);
  }
});
