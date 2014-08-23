package com.donnemartin.android.notes.notes;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NoteListFragment extends ListFragment
{
    private ArrayList<Note> mNotes;

    private static final String TAG = "NoteListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.notes_title);
        mNotes = Notebook.getInstance(getActivity()).getNotes();

        NoteAdapter adapter = new NoteAdapter(mNotes);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        Note note = ((NoteAdapter)getListAdapter()).getItem(position);

        // Start NotePagerActivity with this note
        // NoteListFragment uses getActivity() to pass its hosting
        // activity as the Context object that the Intent constructor needs
        Intent intent = new Intent(getActivity(), NotePagerActivity.class);
        intent.putExtra(NoteFragment.EXTRA_NOTE_ID, note.getId());
        startActivity(intent);
    }

    @Override
    public void onResume()
    // Update the list view onResume, as it might have been paused not killed
    {
        super.onResume();
        ((NoteAdapter)getListAdapter()).notifyDataSetChanged();
    }

    private class NoteAdapter extends ArrayAdapter<Note>
    {
        public NoteAdapter(ArrayList<Note> notes)
        {
            // Required to properly hook up dataset of Notes
            // Not using a pre-defined layout, so pass 0 for the layout ID
            super(getActivity(), 0, notes);
        }

        private String getFormattedDate(FragmentActivity activity,
                                        Note note)
        // XXX: Duplicated code, original in NoteFragment.java
        {
            String formattedDate = "";

            if (activity != null)
            {
                Date date = note.getDate();
                DateFormat dateFormat = android.text.format.DateFormat
                        .getDateFormat(activity.getApplicationContext());
                DateFormat timeFormat = android.text.format.DateFormat
                        .getTimeFormat(activity.getApplicationContext());
                formattedDate = dateFormat.format(date) +
                                " " +
                                timeFormat.format(date);
            }

            return formattedDate;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // If we weren't give a view, inflate one
            if (convertView == null)
            {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.list_item_note, null);
            }

            // Configure the view for this Note
            Note note = getItem(position);

            TextView titleTextView = (TextView) convertView
                    .findViewById(R.id.note_list_item_titleTextView);
            titleTextView.setText(note.getTitle());

            TextView dateTextView = (TextView) convertView
                    .findViewById(R.id.note_list_item_dateTextView);
            dateTextView.setText(getFormattedDate(getActivity(), note));

            CheckBox completeCheckBox = (CheckBox) convertView
                    .findViewById(R.id.note_list_item_completeCheckBox);
            completeCheckBox.setChecked(note.isComplete());

            return convertView;
        }
    }
}
