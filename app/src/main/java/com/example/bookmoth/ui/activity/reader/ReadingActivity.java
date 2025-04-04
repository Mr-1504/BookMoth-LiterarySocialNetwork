package com.example.bookmoth.ui.activity.reader;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.example.bookmoth.R;
import com.example.bookmoth.core.libraryutils.LibraryConst;
import com.example.bookmoth.core.libraryutils.ErrorDialog;
import com.example.bookmoth.core.libraryutils.InnerToast;
import com.example.bookmoth.core.libraryutils.UniversalAnimate;
import com.example.bookmoth.core.libraryutils.ValueGen;
import com.example.bookmoth.data.repository.library.SharedPreferencesRepository;
import com.example.bookmoth.domain.model.library.Chapter;
import com.example.bookmoth.domain.usecase.library.ManageSettingUseCase;
import com.example.bookmoth.ui.viewmodel.reader.ReaderChapterListViewModel;
import com.example.bookmoth.ui.viewmodel.reader.ReaderColorAdjustViewModel;
import com.example.bookmoth.ui.viewmodel.reader.ReaderMainViewModel;
import com.example.bookmoth.ui.viewmodel.reader.ReaderScrollViewModel;
import com.example.bookmoth.ui.viewmodel.reader.ReaderTextFormatViewModel;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import io.noties.markwon.Markwon;

public class ReadingActivity extends AppCompatActivity {
    private ReaderMainViewModel readerViewModel;
    private ReaderScrollViewModel scrollViewModel;
    private ReaderTextFormatViewModel textFormatViewModel;
    private ReaderColorAdjustViewModel colorAdjustViewModel;
    private ReaderChapterListViewModel chaptersViewModel;
    private FragmentManager fragmentManager;
    private SharedPreferences readerSettings;

    private NestedScrollView nestedContainer;
    private BottomAppBar headerBar;
    private BottomAppBar bottomBar;
    private TextView contentView;
    private FrameLayout brightnessFilter;

    private FrameLayout rightExpansion;
    private ScrollFragment scrollFragment;

    private final int EXPANSION_NONE = 0;
    private final int EXPANSION_TEXTFORMAT = 1;
    private final int EXPANSION_COLORADJUST = 2;
    private final int EXPANSION_CHAPTERLIST = 3;
    private FrameLayout bottomExpansion;
    private Fragment textFormatFragment;
    private Fragment colorAdjustFragment;
    private Fragment chapterListFragment;
    private int nowBottomExpansion;

    private TextView tv_title;
    private TextView tv_chapindex;

    private List<Chapter> chapters;
    private Chapter chapter;
    private int nowIndex;
    private String work_title;
    private Markwon makeMarkwon;

    private Boolean barVisible = true;

    /**
     * Tạo Bundle gồm các giá trị cần thết cho các hoạt động của ReaderActivity
     * @param chapters List of Chapter của related Work
     * @param worktitle Tiêu đề tác phẩm
     * @param chapterIndex Chapter được mở (according to chapter list above)
     * @return Bundle để truyền vào Intent, key="requirement"
     */
    public static Bundle makeRequirementBundle(List<Chapter> chapters, String worktitle, int chapterIndex) {
        Bundle bd = new Bundle();
        bd.putSerializable("chapters", (Serializable) chapters);
        bd.putString("worktitle", worktitle);
        bd.putInt("index", chapterIndex);
        return bd;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reader);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readerSettings = getSharedPreferences("BookmothLib_Reader", MODE_PRIVATE);

        readerViewModel = new ViewModelProvider(this).get(ReaderMainViewModel.class);
        scrollViewModel = new ViewModelProvider(this).get(ReaderScrollViewModel.class);
        textFormatViewModel = new ViewModelProvider(this).get(ReaderTextFormatViewModel.class);
        colorAdjustViewModel = new ViewModelProvider(this).get(ReaderColorAdjustViewModel.class);
        chaptersViewModel = new ViewModelProvider(this).get(ReaderChapterListViewModel.class);

        if (getIntent().getBundleExtra("requirement") != null) {
            Bundle req = getIntent().getBundleExtra("requirement");
            chapters = (List<Chapter>) req.getSerializable("chapters");
            nowIndex = req.getInt("index", 0);
            work_title = req.getString("worktitle");
            chapter = chapters.get(nowIndex);
        }

        initObjects();
        initGraphical();
        initFunctions();
        initLiveData();

        displayHeaderInformation();
    }

    private void initObjects() {
        fragmentManager = getSupportFragmentManager();
        for (Fragment f : fragmentManager.getFragments()) fragmentManager.beginTransaction().remove(f).commit();

        nestedContainer = findViewById(R.id.rdr_nsv_content);
        headerBar = findViewById(R.id.rdr_tb_header);
        bottomBar = findViewById(R.id.rdr_tb_bottom);
        contentView = findViewById(R.id.rdr_tv_content);
        brightnessFilter = findViewById(R.id.rdr_fl_brightnessfilter);

        rightExpansion = findViewById(R.id.rdr_fl_rightexpand);
        bottomExpansion = findViewById(R.id.rdr_fl_bottomexpand);

        makeMarkwon = Markwon.create(this);

        expansionSetup();
    }

    private void expansionSetup() {
        scrollFragment = ScrollFragment.newInstance();
        fragmentManager.beginTransaction().replace(R.id.rdr_fl_rightexpand, scrollFragment).commit();
        rightExpansion.setVisibility(View.GONE);

        textFormatFragment = TextFormatFragment.newInstance();
        colorAdjustFragment = ColorAdjustFragment.newInstance();
        chapterListFragment = ChapterListFragment.newInstance();

        fragmentManager.beginTransaction()
                .add(R.id.rdr_fl_bottomexpand, textFormatFragment, String.valueOf(EXPANSION_TEXTFORMAT))
                .add(R.id.rdr_fl_bottomexpand, colorAdjustFragment, String.valueOf(EXPANSION_COLORADJUST))
                .add(R.id.rdr_fl_bottomexpand, chapterListFragment, String.valueOf(EXPANSION_CHAPTERLIST))
                .hide(textFormatFragment)
                .hide(colorAdjustFragment)
                .hide(chapterListFragment)
                .commit();
    }

    private void setBarsGraphicalVisibility(Boolean bool) {
        if (bool && !barVisible) {
            barVisible = true;
            UniversalAnimate.animateWallHiding(headerBar, UniversalAnimate.PLACEMENT_TOP, false);
            UniversalAnimate.animateWallHiding(bottomBar, UniversalAnimate.PLACEMENT_BOTTOM, false);
            UniversalAnimate.animateWallHiding(rightExpansion, UniversalAnimate.PLACEMENT_END, false);
        } else if (!bool && barVisible) {
            barVisible = false;
            UniversalAnimate.animateWallHiding(headerBar, UniversalAnimate.PLACEMENT_TOP, true);
            UniversalAnimate.animateWallHiding(bottomBar, UniversalAnimate.PLACEMENT_BOTTOM, true);
            UniversalAnimate.animateWallHiding(rightExpansion, UniversalAnimate.PLACEMENT_END, true);
            setBottomExpansionVisibility(false);
        }
    }

    private void setBottomExpansionVisibility(Boolean bool) {
        if (!bool) {
            nowBottomExpansion = EXPANSION_NONE;
            UniversalAnimate.animateWallHiding(bottomExpansion, UniversalAnimate.PLACEMENT_BOTTOM, true);
        } else {
            UniversalAnimate.animateWallHiding(bottomExpansion, UniversalAnimate.PLACEMENT_BOTTOM, false);
        }
    }

    private void showManagedBottomFragments(Fragment showThis) {
        fragmentManager.beginTransaction()
                .hide(textFormatFragment)
                .hide(colorAdjustFragment)
                .hide(chapterListFragment)
                .commit();
        textFormatFragment.getView().setVisibility(View.GONE);
        colorAdjustFragment.getView().setVisibility(View.GONE);
        chapterListFragment.getView().setVisibility(View.GONE);
        fragmentManager.beginTransaction().show(showThis).commit();
    }

    private void initGraphical() {
        headerBar.addView(getLayoutInflater().inflate(R.layout.toolbar_rdr_header, headerBar, false));
        bottomBar.addView(getLayoutInflater().inflate(R.layout.toolbar_rdr_bottom, bottomBar, false));
        tv_title = headerBar.findViewById(R.id.rdr_tv_chaptitle);
        tv_chapindex = headerBar.findViewById(R.id.rdr_tv_chapindex);
        setBottomExpansionVisibility(false);
    }

    private void initFunctions() {
        //hiding toolbars when scrolling, show on start/end of NestedScrollView
        nestedContainer.post(() -> {
            nestedContainer.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (scrollY == 0 || scrollY >= (nestedContainer.getChildAt(0).getHeight() - nestedContainer.getHeight() - 1)) {
                    setBarsGraphicalVisibility(true);
                    scrollViewModel.setViewScrollPosition(scrollY);
                    return;
                }
                if (nowBottomExpansion != EXPANSION_NONE && barVisible) return;
                if (scrollY > oldScrollY) setBarsGraphicalVisibility(false);
                scrollViewModel.setViewScrollPosition(scrollY);
            });
        });

        //toggle toolbars when click content TextView
        contentView.setOnClickListener(view -> {
            setBarsGraphicalVisibility(!barVisible);
        });
        findViewById(R.id.rdr_ll_content).setOnClickListener(v -> {
            setBarsGraphicalVisibility(!barVisible);
        });

        //setup custom function when selecting text (also remove copy function)
        contentView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                menu.clear();
                menu.add(0, 1, 0, R.string.contentselection_discuss).setOnMenuItemClickListener(item -> {
                    Toast.makeText(contentView.getContext(), "in-dev, post related", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                });
                menu.add(0, 2, 1, R.string.contentselection_search).setOnMenuItemClickListener(item -> {
                    Toast.makeText(contentView.getContext(), "in-dev, post/store related", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                });
                menu.add(0, 3, 2, R.string.contentselection_report).setOnMenuItemClickListener(item -> {
                    Toast.makeText(contentView.getContext(), "in-dev, community related", Toast.LENGTH_SHORT).show();
                    actionMode.finish();
                    return true;
                });
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {

            }
        });

        //back button
        headerBar.findViewById(R.id.imgbtn_back).setOnClickListener(v -> {
            finish();
        });

        //next and previous chapter buttons
        bottomBar.findViewById(R.id.rdr_imgbtn_next).setOnClickListener(v -> {
            if (!fetchNextChapter())
                InnerToast.show(this, getString(R.string.reader_nextchapter_none));
            else
                InnerToast.show(this, String.format(Locale.getDefault(), "%s %d", getString(R.string.reader_nextchapter_done), nowIndex+1));
        });
        bottomBar.findViewById(R.id.rdr_imgbtn_prev).setOnClickListener(v -> {
            if (!fetchPrevChapter())
                InnerToast.show(this, getString(R.string.reader_prevchapter_none));
            else
                InnerToast.show(this, String.format(Locale.getDefault(), "%s %d", getString(R.string.reader_prevchapter_done), nowIndex+1));
        });

        bottomBar.findViewById(R.id.rdr_imgbtn_textformat).setOnClickListener(v -> {
            switch (nowBottomExpansion) {
                case EXPANSION_NONE:
                    nowBottomExpansion = EXPANSION_TEXTFORMAT;
                    showManagedBottomFragments(textFormatFragment);
                    setBottomExpansionVisibility(true);
                    break;
                default:
                    setBottomExpansionVisibility(false);
                    break;
            }
        });

        bottomBar.findViewById(R.id.rdr_imgbtn_coloradjust).setOnClickListener(v -> {
            switch (nowBottomExpansion) {
                case EXPANSION_NONE:
                    nowBottomExpansion = EXPANSION_COLORADJUST;
                    showManagedBottomFragments(colorAdjustFragment);
                    setBottomExpansionVisibility(true);
                    break;
                default:
                    setBottomExpansionVisibility(false);
                    break;
            }
        });

        bottomBar.findViewById(R.id.rdr_imgbtn_chaplist).setOnClickListener(v -> {
            switch (nowBottomExpansion) {
                case EXPANSION_NONE:
                    nowBottomExpansion = EXPANSION_CHAPTERLIST;
                    showManagedBottomFragments(chapterListFragment);
                    setBottomExpansionVisibility(true);
                    break;
                default:
                    setBottomExpansionVisibility(false);
                    break;
            }
        });
    }

    private void initLiveData() {
        scrollViewModel.getBarScrollPosition().observe(this, v -> nestedContainer.scrollTo(0, v));

        textFormatViewModel.getTextSize().observe(this, contentView::setTextSize);
        textFormatViewModel.getFontFamily().observe(this, v -> contentView.setTypeface(ResourcesCompat.getFont(this, v)));

        colorAdjustViewModel.getBrightness().observe(this, v -> brightnessFilter.setBackgroundColor(Color.parseColor(ValueGen.makeTransparencyParseColorValue(v, colorAdjustViewModel.getColorTint().getValue()))));
        colorAdjustViewModel.getTextColor().observe(this, v -> contentView.setTextColor(v));
        colorAdjustViewModel.getBackgroundColor().observe(this, v -> nestedContainer.setBackgroundColor(v));

        loadExpansionData();

        readerViewModel.getMarkdownString().observe(this, v -> {
            makeMarkwon.setMarkdown(contentView, v);
            initOnContentLoaded();
        });
        readerViewModel.getMessage().observe(this, v -> {
            if (!v.isBlank()) {
                ErrorDialog.showError(this, v);
            }
        });
    }

    private void loadExpansionData() {
        ManageSettingUseCase settingUseCase = new ManageSettingUseCase(new SharedPreferencesRepository(getSharedPreferences(LibraryConst.SHAREDPREFS_NAME, MODE_PRIVATE)));
        textFormatViewModel.loadSettings(settingUseCase);
        colorAdjustViewModel.loadSettings(settingUseCase);

        contentView.setTypeface(ResourcesCompat.getFont(this, textFormatViewModel.getFontFamily().getValue()));
        contentView.setTextSize(textFormatViewModel.getTextSize().getValue());
        brightnessFilter.setBackgroundColor(Color.parseColor(ValueGen.makeTransparencyParseColorValue(colorAdjustViewModel.getBrightness().getValue(), colorAdjustViewModel.getColorTint().getValue())));
        contentView.setTextColor(colorAdjustViewModel.getTextColor().getValue());
        nestedContainer.setBackgroundColor(colorAdjustViewModel.getBackgroundColor().getValue());

        chaptersViewModel.setChapterCounts(chapters.size());
        chaptersViewModel.setNowChapterIndex(nowIndex);
        chaptersViewModel.getNowChapterIndex().observe(this, v -> {
            fetchChapter(v);
            InnerToast.show(this, String.format(Locale.getDefault(), "%s %d", getString(R.string.reader_loadchaper), v+1));
        });
    }

    private void initOnContentLoaded() {
        nestedContainer.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            rightExpansion.setVisibility(View.VISIBLE);
            scrollViewModel.setHeights(this.getWindow().getDecorView().getHeight(), nestedContainer.getChildAt(0).getHeight() - nestedContainer.getHeight());
        });
    }

    private void displayHeaderInformation() {
        tv_title.setText(chapter.getTitle() != null ? chapter.getTitle() : work_title);
        tv_chapindex.setText(String.format(Locale.getDefault(), "%s %d %s", getString(R.string.chapter_chapter), nowIndex + 1, (chapter.getTitle() == null ? "" : " - " + work_title)));
    }

    private void fetchContent() {
        readerViewModel.fetchChapterContent(chapter);
    }

    private boolean fetchChapter(int index) {
        nowIndex = index;
        chapter = chapters.get(index);
        fetchContent();
        displayHeaderInformation();
        return true;
    }

    private boolean fetchNextChapter() {
        if (nowIndex >= chapters.size() - 1) {
            return false;
        }
        chapter = chapters.get(++nowIndex);
        fetchContent();
        displayHeaderInformation();
        return true;
    }

    private boolean fetchPrevChapter() {
        if (nowIndex <= 0) {
            return false;
        }
        chapter = chapters.get(--nowIndex);
        fetchContent();
        displayHeaderInformation();
        return true;
    }
}