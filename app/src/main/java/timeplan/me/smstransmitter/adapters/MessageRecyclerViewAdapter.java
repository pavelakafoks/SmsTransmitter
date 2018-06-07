package timeplan.me.smstransmitter.adapters;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import timeplan.me.smstransmitter.helpers.DateTimeHelper;
import timeplan.me.smstransmitter.models.EnumMessageStatus;
import timeplan.me.smstransmitter.R;
import timeplan.me.smstransmitter.models.Message;


public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private final List<Message> mValues;

    public MessageRecyclerViewAdapter(List<Message> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        Message message = mValues.get(position);

        String dtSend = DateTimeHelper.ToString(message.DtSend);
        if (DateTimeHelper.IsDefaultDate(dtSend)){
            dtSend = "";
        }

        holder.mPhoneView.setText(message.PhoneFormatted());
        holder.mNameView.setText(message.Name);
        holder.mDtSend.setText(dtSend);
        holder.mMessageView.setText(message.Message);
        if(message.StatusId == EnumMessageStatus.Sent) {
            Uri uri = Uri.parse("android.resource://timeplan.me.smstransmitter/" + android.R.drawable.presence_online);
            holder.mStatusImage.setImageURI(uri);
        }
        else if(message.StatusId == EnumMessageStatus.Error) {
            Uri uri = Uri.parse("android.resource://timeplan.me.smstransmitter/" + android.R.drawable.presence_busy);
            holder.mStatusImage.setImageURI(uri);
        }

        /*
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.

                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
        */
    }


    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mPhoneView;
        public final TextView mNameView;
        public final TextView mDtSend;
        public final TextView mMessageView;
        public final ImageView mStatusImage;

        public ViewHolder(View view) {
            super(view);
            mPhoneView = (TextView) view.findViewById(R.id.Phone);
            mNameView = (TextView) view.findViewById(R.id.Name);
            mDtSend = (TextView) view.findViewById(R.id.DtSend);
            mMessageView = (TextView) view.findViewById(R.id.Message);
            mStatusImage = (ImageView) view.findViewById(R.id.StatusImage);
        }

        /* @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }*/
    }
}
