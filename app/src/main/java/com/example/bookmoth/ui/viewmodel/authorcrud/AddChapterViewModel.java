package com.example.bookmoth.ui.viewmodel.authorcrud;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.usecase.library.AddChapterUseCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddChapterViewModel extends ViewModel {
    private final AddChapterUseCase addChapter = new AddChapterUseCase(new LibApiRepository());

    private final MutableLiveData<Bundle> infoBundle = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public void setInfoBundle(Bundle infos, Context context) {
        infoBundle.setValue(infos);

        String token = infos.getString("credential");

        File contentFile = null;
        if (infos.getParcelable("content_uri") != null) try {
            InputStream inpStream = context.getContentResolver().openInputStream(infos.getParcelable("content_uri"));
            if (!(inpStream == null)) {
                contentFile = new File(context.getCacheDir(), "contentFile");
                FileOutputStream outStream = new FileOutputStream(contentFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inpStream.read(buffer)) > 0) {
                    outStream.write(buffer, 0, length);
                }
                outStream.close();
            }
            inpStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Chapter chapter = new Chapter();
        chapter.setTitle(infos.getString("title"));

        int work_id = infos.getInt("work_id");

        String filename = infos.getString("filename");

        addChapter.run(token, work_id, contentFile, filename, chapter, new InnerCallback<String>() {
            @Override
            public void onSuccess(String body) {
                message.setValue("");
            }

            @Override
            public void onError(String errorMessage) {
                message.setValue(errorMessage);
            }
        });
    }

    public LiveData<String> getMessage() {return message;}
}
