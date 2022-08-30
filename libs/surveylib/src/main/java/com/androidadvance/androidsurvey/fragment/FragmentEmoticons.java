package com.androidadvance.androidsurvey.fragment;

import android.app.Service;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.androidadvance.androidsurvey.Answers;
import com.androidadvance.androidsurvey.R;
import com.androidadvance.androidsurvey.SurveyActivity;
import com.androidadvance.androidsurvey.adapters.EmoticonAdapter;
import com.androidadvance.androidsurvey.models.Question;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class FragmentEmoticons extends Fragment {

    private FragmentActivity mContext;
    private Button button_continue, button_previous;
    private TextView textview_q_title, emotion;
    private GridView emoticonsView;
    private EmoticonAdapter emoticonAdapter;
    private boolean at_leaset_one_checked = false;
    private List<String> qq_data;
    private int selectedIndex = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_emoticons, container, false);

        button_continue = (Button) rootView.findViewById(R.id.button_continue);
        button_previous = (Button) rootView.findViewById(R.id.button_previous);
        textview_q_title = (TextView) rootView.findViewById(R.id.textview_q_title);
        emotion = (TextView) rootView.findViewById(R.id.emotion);
        emoticonsView = rootView.findViewById(R.id.emoticon_view);
        emoticonAdapter = new EmoticonAdapter(getContext(), emotion);

        emoticonsView.setAdapter(emoticonAdapter);
        button_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Answers.getInstance().put_answer(textview_q_title.getText().toString(), emoticonAdapter.getItem(emoticonAdapter.getSelectedIndex()).split(",")[0]);
                ((SurveyActivity) mContext).goToNext();
            }
        });

        button_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((SurveyActivity) mContext).goToPrevious();
            }
        });


        emoticonsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //selectedIndex = position;

               emoticonAdapter.setSelectedIndex(position);
                button_continue.setEnabled(true);
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = getActivity();
        Question q_data = (Question) getArguments().getSerializable("data");

        textview_q_title.setText(Html.fromHtml(q_data.getQuestionTitle()));

        qq_data = q_data.getChoices();
        if (q_data.getRandomChoices()) {
            Collections.shuffle(qq_data);
        }

        emoticonAdapter.setList(qq_data);

        if (q_data.getRequired()) {
            if (at_leaset_one_checked) {
                button_continue.setEnabled(true);
            } else {
                button_continue.setEnabled(false);
            }
        }
    }
}
