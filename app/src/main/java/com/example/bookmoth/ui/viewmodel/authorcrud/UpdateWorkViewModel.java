package com.example.bookmoth.ui.viewmodel.authorcrud;

import android.content.Context;
import android.os.Bundle;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.domain.usecase.library.UpdateWorkUseCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class UpdateWorkViewModel extends ViewModel {
    private final UpdateWorkUseCase updateWork = new UpdateWorkUseCase(new LibApiRepository());
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<String> getMessage() {return message;}

    public void doUpdateWork(Bundle infos, Context context) {
        File coverImage = null;
        if (infos.getParcelable("cover_uri") != null) try {
            InputStream inpStream = context.getContentResolver().openInputStream(infos.getParcelable("cover_uri"));
            if (!(inpStream == null)) {
                coverImage = new File(context.getCacheDir(), "coverImage");
                FileOutputStream outStream = new FileOutputStream(coverImage);
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

        Work workInfos = new Work();
        workInfos.setProfile_id(LibraryConst.PROFILE_ID);
        if (infos.containsKey("title")) workInfos.setTitle(infos.getString("title"));
        if (infos.containsKey("price")) workInfos.setPrice((double) infos.getInt("price"));
        if (infos.containsKey("description")) workInfos.setDescription(infos.getString("description"));

        updateWork.run(LibraryConst.TEST_TOKEN, infos.getInt("work_id"), coverImage, workInfos, new InnerCallback<String>() {
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
}
