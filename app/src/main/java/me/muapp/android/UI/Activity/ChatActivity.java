package me.muapp.android.UI.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Chat.ChatReferences;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Chat.Message;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.MessagesAdapter;
import me.muapp.android.UI.Fragment.AddAttachmentDialogFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

public class ChatActivity extends BaseActivity implements ChildEventListener, AddAttachmentDialogFragment.ChatAttachmentListener {
    public static final String CONVERSATION_EXTRA = "CONVERSATION_EXTRA";
    public static final String CONTENT_FROM_CHAT = "CONTENT_FROM_CHAT";
    private static final int ATTACHMENT_PICTURE = 911;
    private static final int REQUEST_GALLERY_PERMISSIONS = 470;
    ConversationItem conversationItem;
    Toolbar toolbar;
    TextView toolbar_opponent_fullname;
    RecyclerView recycler_conversation;
    DatabaseReference conversationReference;
    MessagesAdapter messagesAdapter;
    EditText etMessage;
    ImageButton chatSendButton;
    ImageButton chatAddAttachmentButton;
    DatabaseReference myConversation, yourConversation, yourPresence;
    StorageReference myStorageReference;
    ChatReferences chatReferences;
    ValueEventListener presenceListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            boolean imOnline = dataSnapshot.getValue(Boolean.class);
            Drawable img = ContextCompat.getDrawable(ChatActivity.this, imOnline ? R.drawable.ic_chat_user_online : R.drawable.ic_chat_user_offline);
            toolbar_opponent_fullname.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        }


        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private boolean shouldSendToSettingsChatPermissions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        conversationItem = getIntent().getParcelableExtra(CONVERSATION_EXTRA);
        if (conversationItem == null)
            finish();
        myStorageReference = FirebaseStorage.getInstance().getReference().child(String.valueOf(loggedUser.getId())).child("conversations").child(conversationItem.getKey());
        yourPresence = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(conversationItem.getConversation().getOpponentId())).child("online");
        messagesAdapter = new MessagesAdapter(this);
        messagesAdapter.setLoggedUserId(loggedUser.getId());
        conversationReference = /*FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId()))
                .child(conversationItem.getKey())
                .child("conversation");*/
                FirebaseDatabase.getInstance().getReference().child("JW")
                        .child(String.valueOf(loggedUser.getId()))
                        .child(conversationItem.getKey())
                        .child("conversation");


        myConversation = /*FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId()))
                .child(conversationItem.getKey());
*/
                FirebaseDatabase.getInstance().getReference().child("JW")
                        .child(String.valueOf(loggedUser.getId()))
                        .child(conversationItem.getKey());

        Log.wtf("convesration", "mine " + myConversation.getRef().toString());

        yourConversation = /*FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(loggedUser.getId()))
                .child(conversationItem.getConversation().getOpponentConversationId());*/
                FirebaseDatabase.getInstance().getReference().child("JW")
                        .child(String.valueOf(conversationItem.getConversation().getOpponentId()))
                        .child(conversationItem.getConversation().getOpponentConversationId());

        Log.wtf("convesration", "yours " + yourConversation.getRef().toString());

        chatReferences = new ChatReferences(myConversation.getRef().toString(), yourConversation.getRef().toString());

        etMessage = (EditText) findViewById(R.id.etMessage);
        chatSendButton = (ImageButton) findViewById(R.id.chatSendButton);
        chatAddAttachmentButton = (ImageButton) findViewById(R.id.chatAddAttachmentButton);
        toolbar_opponent_fullname = (TextView) findViewById(R.id.toolbar_opponent_fullname);
        toolbar_opponent_fullname.setText(conversationItem.getFullName());
        toolbar.findViewById(R.id.toolbar_btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recycler_conversation = (RecyclerView) findViewById(R.id.recycler_conversation);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recycler_conversation.setLayoutManager(llm);
        recycler_conversation.setAdapter(messagesAdapter);
        Glide.with(this).load(conversationItem.getProfilePicture()).placeholder(R.drawable.ic_logo_muapp_no_caption).bitmapTransform(new CropCircleTransformation(this)).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) toolbar.findViewById(R.id.toolbar_opponent_photo));
        chatSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attempSend(null);
            }
        });
        chatAddAttachmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddAttachmentDialogFragment.newInstance(6).show(getSupportFragmentManager(), "attach");
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        conversationReference.addChildEventListener(this);
        yourPresence.addValueEventListener(presenceListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        conversationReference.removeEventListener(this);
        yourPresence.removeEventListener(presenceListener);
    }


    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Message m = dataSnapshot.getValue(Message.class);
        if (m != null)
            m.setKey(dataSnapshot.getKey());
        Log.wtf("chat", m.toString());
        messagesAdapter.addMessage(m);
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    private void attempSend(UserContent content) {
        if (!TextUtils.isEmpty(etMessage.getText().toString()) || content != null) {
            Log.wtf("Sending message", "");
            Message m = new Message();
            m.setTimeStamp(new Date().getTime());
            m.setSenderId(loggedUser.getId());
            m.setContent(etMessage.getText().toString());
            if (content != null)
                m.setAttachment(content);
            etMessage.setText("");
            conversationReference.child(conversationReference.push().getKey()).setValue(m);
            yourConversation.child("conversation").child(yourConversation.push().getKey()).setValue(m);
            m.setReaded(false);
            myConversation.child("lastMessage").setValue(m);
            yourConversation.child("lastMessage").setValue(m);
            sendPushMessage();
        }
    }

    private void sendPushMessage() {
        if (!TextUtils.isEmpty(conversationItem.getPushToken())) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject sendObject = new JSONObject();
            try {
                sendObject.put("to", conversationItem.getPushToken());
                sendObject.put("collapse_key", conversationItem.getKey());
                sendObject.put("priority", "high");
                sendObject.put("content_available", true);
                JSONObject notification = new JSONObject();
                notification.put("tag", conversationItem.getKey());
                notification.put("title", getString(R.string.app_name));
                notification.put("color", "#ff666e");
                notification.put("sound", "default");
                notification.put("body_loc_key", "notif_sent_message");
                notification.put("body_loc_args", new JSONArray(new String[]{conversationItem.getName()}));
                sendObject.put("notification", notification);
            } catch (Exception x) {

            }
            RequestBody body = RequestBody.create(mediaType, sendObject.toString());
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .addHeader("authorization", "key=AIzaSyCAPtbJ8ZFXLF8ot_hyadW2_zqD9E9fMkE")
                    .addHeader("content-type", "application/json")
                    .build();
            Log.wtf("sendPushMessage", sendObject.toString());
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }
    }

    @Override
    public void onChatAttachmentItemClicked(int position) {
        switch (position) {
            //Pictures
            case 2:
                if (checkAndRequestGalleryPermissions())
                    galleryIntent();
                break;
            case 3:
                Intent giphyIntent = new Intent(this, AddGiphyActivity.class);
                giphyIntent.putExtra(CONTENT_FROM_CHAT, chatReferences);
                startActivity(giphyIntent);
                break;
            case 4:
                Intent spotifyIntent = new Intent(this, AddSpotifyActivity.class);
                spotifyIntent.putExtra(CONTENT_FROM_CHAT, chatReferences);
                startActivity(spotifyIntent);
            case 5:
                Intent youtubeIntent = new Intent(this, AddYoutubeActivity.class);
                youtubeIntent.putExtra(CONTENT_FROM_CHAT, chatReferences);
                startActivity(youtubeIntent);
        }

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Select File"), ATTACHMENT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ATTACHMENT_PICTURE:
                    Log.wtf("attach", "picture started");
                    onSelectFromGalleryResult(data);
                    break;
                default:
                    sendPushMessage();
                    break;
            }
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        File f = null;
        String filePath = "";
        Uri selectedImage = null;
        if (data != null && data.getData() != null) {
            try {
                selectedImage = data.getData();
                Log.v("file", selectedImage.toString());
                String wholeID = DocumentsContract.getDocumentId(selectedImage);
                String id = wholeID.split(":")[1];
                String[] column = {MediaStore.Images.Media.DATA};
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                    Log.v("filePath", filePath);
                    compressAndUpload(f);
                } else {
                }
                cursor.close();
                Log.wtf("selected", f.getAbsolutePath());
            } catch (Exception x) {
                try {
                    f = new File(getRealPathFromURI(selectedImage));
                    compressAndUpload(f);
                } catch (Exception ex) {

                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    private void compressAndUpload(File f) {
        final UserContent thisContent = new UserContent();
        Log.wtf("selected", f.getAbsolutePath());
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(), options);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
        myStorageReference = myStorageReference.child("media" + new Date().getTime());
        UploadTask uploadTask = myStorageReference.putBytes(out.toByteArray());
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    @SuppressWarnings("VisibleForTests") Uri downloadUrl = task.getResult().getDownloadUrl();
                    thisContent.setComment("");
                    thisContent.setCreatedAt(new Date().getTime());
                    thisContent.setCatContent("contentPic");
                    thisContent.setContentUrl(downloadUrl.toString());
                    thisContent.setStorageName(myStorageReference.getPath());
                    attempSend(thisContent);
                }
            }
        });
    }


    private boolean checkAndRequestGalleryPermissions() {
        int permissionRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionRead != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(ChatActivity.this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_GALLERY_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        boolean permissionsGranted = true;
        switch (requestCode) {
            case REQUEST_GALLERY_PERMISSIONS:
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        permissionsGranted = false;
                        // user rejected the permission
                        boolean showRationale = shouldShowRequestPermissionRationale(permission);
                        if (!showRationale) {
                            // user also CHECKED "never ask again"
                            shouldSendToSettingsChatPermissions = true;
                            break;
                        }
                    }
                }
                if (permissionsGranted) {
                    galleryIntent();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}


