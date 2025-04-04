package com.example.bookmoth.ui.viewmodel.authorcrud;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.core.libraryutils.InnerCallback;
import com.example.bookmoth.data.repository.library.LibApiRepository;
import com.example.bookmoth.domain.model.library.Work;
import com.example.bookmoth.domain.usecase.library.AddWorkUseCase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddWorkViewModel extends AndroidViewModel {
    private final AddWorkUseCase createWork = new AddWorkUseCase(new LibApiRepository());

    private final MutableLiveData<Bundle> infoBundle = new MutableLiveData<>();
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public AddWorkViewModel(@NonNull Application application) {
        super(application);
    }

    public void setInfoBundle(Bundle infos, Context context) {
        infoBundle.setValue(infos);

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

        Work work = new Work();
        work.setTitle(infos.getString("title"));
        work.setPrice((double) infos.getInt("price"));
        work.setDescription(infos.getString("description"));

        createWork.run(coverImage, work, new InnerCallback<String>() {
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
