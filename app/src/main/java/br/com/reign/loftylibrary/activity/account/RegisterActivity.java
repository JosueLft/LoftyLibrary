package br.com.reign.loftylibrary.activity.account;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import br.com.reign.loftylibrary.R;
import br.com.reign.loftylibrary.activity.settings.SettingsActivity;
import br.com.reign.loftylibrary.model.User;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText txtRegisterName;
    private TextInputEditText txtRegisterEmail;
    private TextInputEditText txtRegisterPassword;
    private Button btnRegister;
    private Button btnSelectImage;
    private Uri selectedUri;
    private ImageView imgPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        txtRegisterName = findViewById(R.id.txtRegisterName);
        txtRegisterEmail = findViewById(R.id.txtRegisterEmail);
        txtRegisterPassword = findViewById(R.id.txtRegisterPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        imgPhoto = findViewById(R.id.imgPhoto);

        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPhoto();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createUser();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 0) {
            selectedUri = data.getData();

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedUri);
                imgPhoto.setImageDrawable(new BitmapDrawable(bitmap));
                btnSelectImage.setAlpha(0);// escondendo botão tornando o transparente
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void selectPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void createUser() {
        String name = txtRegisterName.getText().toString();
        String email = txtRegisterEmail.getText().toString();
        String password = txtRegisterPassword.getText().toString();

        if(name == null || name.isEmpty() || email == null || email.isEmpty() || password == null || password.isEmpty()) {
            Toast.makeText(
                    this,
                    "Preencha todos os campos para se cadastrar!",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "Realizando registro, por favor aguarde!",
                                    Toast.LENGTH_LONG
                            ).show();
                            saveUserInFirebase();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.i("RegisterError", e.getMessage());
                        if(e.getMessage().equals("The email address is already in use by another account.")) {
                            Toast.makeText(
                                    getApplicationContext(),
                                    "E-mail ja esta em uso!",
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }
                });
    }

    private void saveUserInFirebase() {
        String filename = UUID.randomUUID().toString();
        final StorageReference ref = FirebaseStorage.getInstance().getReference("/accountImages/" + filename);
                ref.putFile(selectedUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.i("RegisterInformation", uri.toString());

                                String id = FirebaseAuth.getInstance().getUid();
                                String name = txtRegisterName.getText().toString();
                                String profileUrl = uri.toString();

                                User user = new User(id, name, profileUrl);

                                FirebaseFirestore
                                        .getInstance()
                                        .collection("users")
                                        .document(id)
                                        .set(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Intent intent = new Intent(RegisterActivity.this, SettingsActivity.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "Cadastro realizado com sucesso!!",
                                                        Toast.LENGTH_LONG
                                                ).show();
                                                Toast.makeText(
                                                        getApplicationContext(),
                                                        "Seja Bem-vindo " + txtRegisterName.getText().toString() + "!!",
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e("RegisterError", e.getMessage(), e);
                                            }
                                        });
                                txtRegisterName.setText("");
                                txtRegisterEmail.setText("");
                                txtRegisterPassword.setText("");
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("RegisterError", e.getMessage(), e);
                    }
                });
    }
}