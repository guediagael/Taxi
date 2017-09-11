package android.theletch.tech.taxi;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by guedi on 9/9/2017.
 */

public class CommentDialogFragment  extends DialogFragment {

    public static final String TAG = "CommentDialogFragment";
    private DialogListener mListener;
    private EditText etComment;
    private InputMethodManager imm;
    private String text ;


    public static CommentDialogFragment newInstance(String comment){
        CommentDialogFragment fragment = new CommentDialogFragment();
        Bundle arg = new Bundle();
        if (comment!=null)
        arg.putString(TAG,comment);
        fragment.setArguments(arg);
        return  fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try{
            text = getArguments().getString(TAG);
        }catch (NullPointerException e){

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_comment_dialog, container, false);

        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.prompt_comment));

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
//            actionBar.setHomeAsUpIndicator(android.R.drawable.ic_menu_close_clear_cancel);
        }
        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etComment = view.findViewById(R.id.text_dialog_comment);
        if (text!=null && text.length()>1) etComment.setText(text);
        etComment.requestFocus();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(etComment,InputMethodManager.SHOW_IMPLICIT);

        etComment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE ||
                        ((keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) &&
                                (keyEvent.getAction() == KeyEvent.ACTION_DOWN)) ||
                        i == EditorInfo.IME_ACTION_NEXT){
                    checkText();
                }

                return true;
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogListener)
            mListener = (DialogListener) context;
        else throw new ClassCastException(context.getPackageCodePath()+" must implement " +
                "DialogListener");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // handle close button click here
           checkText();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkText(){
        imm.hideSoftInputFromWindow(etComment.getWindowToken(),0);
        String comment = etComment.getText().toString().trim();
        if ( comment !=null && comment.length()>1) mListener.sendComment(comment);
        else mListener.setCommentEmpty();
        dismiss();
    }


    public interface DialogListener{
        void sendComment(String comment);
        void setCommentEmpty();
    }
}
