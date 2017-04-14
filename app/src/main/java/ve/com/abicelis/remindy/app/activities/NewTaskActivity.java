package ve.com.abicelis.remindy.app.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.transitionseverywhere.Slide;
import com.transitionseverywhere.TransitionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ve.com.abicelis.remindy.R;
import ve.com.abicelis.remindy.app.adapters.AttachmentAdapter;
import ve.com.abicelis.remindy.enums.AttachmentType;
import ve.com.abicelis.remindy.enums.TaskCategory;
import ve.com.abicelis.remindy.model.Task;
import ve.com.abicelis.remindy.model.attachment.Attachment;
import ve.com.abicelis.remindy.model.attachment.AudioAttachment;
import ve.com.abicelis.remindy.model.attachment.LinkAttachment;
import ve.com.abicelis.remindy.model.attachment.TextAttachment;
import ve.com.abicelis.remindy.util.ConversionUtil;
import ve.com.abicelis.remindy.util.FileUtil;

/**
 * Created by abice on 13/4/2017.
 */

public class NewTaskActivity extends AppCompatActivity implements View.OnClickListener {

    //CONST

    //DATA
    private List<String> reminderCategories;
    private int addAttachmentHintState = 0;
    private Task mTask = new Task();
    private AttachmentAdapter mAdapter;

    //UI
    private Toolbar mToolbar;
    private RelativeLayout mHeaderBasicInfo;
    private RelativeLayout mHeaderAttachments;
    private LinearLayout mContainer;
    private LinearLayout mContainerBasicInfo;
    private TextView mTaskTitle;
    private TextView mTaskDescription;
    private Spinner mTaskCategory;
    private TextView mAttachmentsFabHint;
    private ScrollView mScrollView;
    private FloatingActionMenu mAttachmentsFabMenu;
    private FloatingActionButton mAttachmentsFabList;
    private FloatingActionButton mAttachmentsFabText;
    private FloatingActionButton mAttachmentsFabLink;
    private FloatingActionButton mAttachmentsFabImage;
    private FloatingActionButton mAttachmentsFabAudio;

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private RelativeLayout mNoItemsContainer;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);


        mHeaderBasicInfo = (RelativeLayout) findViewById(R.id.activity_new_task_header_basic_info);
        ((TextView) mHeaderBasicInfo.findViewById(R.id.item_task_header_title)).setText(R.string.activity_new_task_header_basic_info);

        mHeaderAttachments = (RelativeLayout) findViewById(R.id.activity_new_task_header_attachments);
        ((TextView) mHeaderAttachments.findViewById(R.id.item_task_header_title)).setText(R.string.activity_new_task_header_attachments);

        mContainer = (LinearLayout) findViewById(R.id.activity_new_task_container);
        mContainerBasicInfo = (LinearLayout) findViewById(R.id.activity_new_task_basic_info_container);
        mTaskTitle = (TextView) findViewById(R.id.activity_new_task_title);
        mTaskTitle.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    mAttachmentsFabMenu.close(true);
                }
            }
        });
        mTaskDescription = (TextView) findViewById(R.id.activity_new_task_description);
        mTaskDescription.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    mAttachmentsFabMenu.close(true);
                }
            }
        });
        mTaskCategory = (Spinner) findViewById(R.id.activity_new_task_category);
        //mAttachmentsFabHintContainer = (FrameLayout) findViewById(R.id.activity_new_task_add_attachment_hint_container);
        mAttachmentsFabHint = (TextView) findViewById(R.id.activity_new_task_add_attachment_hint);
        mScrollView = (ScrollView) findViewById(R.id.activity_new_task_scrollview);

        mAttachmentsFabMenu = (FloatingActionMenu) findViewById(R.id.activity_new_task_add_attachment);
        mAttachmentsFabMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                //Hide keyboard
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mContainer.getWindowToken(), 0);

                if(addAttachmentHintState == 1) {     //Slide out FAB hint
                    addAttachmentHintState = 2;

                    TransitionManager.beginDelayedTransition(mContainer, new Slide(Gravity.START));
                    mAttachmentsFabHint.setVisibility(View.INVISIBLE);
                }
            }
        });
        mAttachmentsFabList = (FloatingActionButton) findViewById(R.id.activity_new_task_add_list_attachment);
        mAttachmentsFabText = (FloatingActionButton) findViewById(R.id.activity_new_task_add_text_attachment);
        mAttachmentsFabLink = (FloatingActionButton) findViewById(R.id.activity_new_task_add_link_attachment);
        mAttachmentsFabImage = (FloatingActionButton) findViewById(R.id.activity_new_task_add_image_attachment);
        mAttachmentsFabAudio = (FloatingActionButton) findViewById(R.id.activity_new_task_add_audio_attachment);

        mAttachmentsFabList.setOnClickListener(this);
        mAttachmentsFabText.setOnClickListener(this);
        mAttachmentsFabLink.setOnClickListener(this);
        mAttachmentsFabImage.setOnClickListener(this);
        mAttachmentsFabAudio.setOnClickListener(this);


        mRecyclerView = (RecyclerView) findViewById(R.id.activity_new_task_recycler);
        mNoItemsContainer = (RelativeLayout) findViewById(R.id.activity_new_task_no_items_container);

        setUpRecyclerView();
        setupSpinners();
        setUpToolbar();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        //Slide in FAB hint
        if(addAttachmentHintState == 0) {
            addAttachmentHintState = 1;
            TransitionManager.beginDelayedTransition(mContainer, new Slide(Gravity.START));
            mAttachmentsFabHint.setVisibility(View.VISIBLE);
        }

    }


    private void setUpRecyclerView() {

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mAdapter = new AttachmentAdapter(this, mTask.getAttachments());
        DividerItemDecoration itemDecoration = new DividerItemDecoration(this, mLayoutManager.getOrientation());
        itemDecoration.setDrawable(ContextCompat.getDrawable(this, R.drawable.item_decoration_half_line));

        mRecyclerView.addItemDecoration(itemDecoration);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int position = viewHolder.getAdapterPosition();
                Toast.makeText(NewTaskActivity.this, "Swiped position " + position + " into direction=" + swipeDir, Toast.LENGTH_SHORT).show();

                if(AttachmentType.AUDIO.equals(mTask.getAttachments().get(position).getType())) {
                    String filename = ((AudioAttachment)mTask.getAttachments().get(position)).getAudioFilename();

                    if(!filename.isEmpty()) { //Delete file
                        File audioAttachmentDir = FileUtil.getAudioAttachmentDir(NewTaskActivity.this);
                        File audioFile = new File(audioAttachmentDir, filename);
                        audioFile.delete();
                    }
                }

                mTask.getAttachments().remove(position);
                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        //itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.activity_new_task_toolbar);
        mToolbar.setTitle(getResources().getString( R.string.activity_new_task_toolbar_title));
        mToolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.icon_back_material));

        //Set toolbar as actionbar
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupSpinners() {
        reminderCategories = TaskCategory.getFriendlyValues(this);
        ArrayAdapter reminderCategoryAdapter = new ArrayAdapter<>(this, R.layout.spinner_item, reminderCategories);
        reminderCategoryAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        mTaskCategory.setAdapter(reminderCategoryAdapter);
    }


    private void addAttachment(Attachment attachment) {
        mTask.addAttachment(attachment);
        if(mAdapter.getItemCount() == 1)
            mAdapter.notifyDataSetChanged();
        else
            mAdapter.notifyItemInserted(mAdapter.getItemCount());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();



        mAttachmentsFabMenu.close(true);

        if(addAttachmentHintState == 2) {
            addAttachmentHintState = 3;

            //Fade in headers
            TransitionManager.beginDelayedTransition(mContainer);
            mHeaderBasicInfo.setVisibility(View.VISIBLE);
            mHeaderAttachments.setVisibility(View.VISIBLE);

            RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mContainerBasicInfo.getLayoutParams();
            lp.setMargins(ConversionUtil.dpToPx(16, getResources()), 0, 0, 0);
            mContainerBasicInfo.setLayoutParams(lp);

        }

        switch (id) {
            case R.id.activity_new_task_add_list_attachment:
                //TODO: Add list attachment to recycler!
                Toast.makeText(this, "Added list attachment", Toast.LENGTH_SHORT).show();
                break;

            case R.id.activity_new_task_add_text_attachment:
                addAttachment(new TextAttachment(""));
                break;

            case R.id.activity_new_task_add_link_attachment:
                addAttachment(new LinkAttachment(""));
                break;

            case R.id.activity_new_task_add_image_attachment:
                //TODO: Add image attachment to recycler!
                Toast.makeText(this, "Added image attachment", Toast.LENGTH_SHORT).show();
                break;

            case R.id.activity_new_task_add_audio_attachment:
                addAttachment(new AudioAttachment());
                break;
        }


    }

    @Override
    public void onBackPressed() {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.activity_new_task_exit_dialog_title))
                .setMessage(getResources().getString(R.string.activity_new_task_exit_dialog_message))
                .setPositiveButton(getResources().getString(R.string.activity_new_task_exit_dialog_positive),  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegativeButton(getResources().getString(R.string.activity_new_task_exit_dialog_negative), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return false;
    }

}
