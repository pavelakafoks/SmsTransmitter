package timeplan.me.smstransmitter.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import timeplan.me.smstransmitter.helpers.DateTimeHelper;
import timeplan.me.smstransmitter.models.EnumLogType;
import timeplan.me.smstransmitter.R;
import timeplan.me.smstransmitter.models.Log;

public class LogRecyclerViewAdapter extends RecyclerView.Adapter<LogRecyclerViewAdapter.ViewHolder> {

    private final List<Log> mValues;

    public LogRecyclerViewAdapter(List<Log> items) {
        mValues = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_log, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log log = mValues.get(position);

        holder.mDtCreate.setText(DateTimeHelper.ToString(log.DtCreate));
        holder.mInformation.setText(log.Information);

        if(log.LogType == EnumLogType.Error) {  // error
            holder.mInformation.setTextColor(Color.RED);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mDtCreate;
        public final TextView mInformation;

        public ViewHolder(View view) {
            super(view);
            mDtCreate = (TextView) view.findViewById(R.id.DtCreate);
            mInformation = (TextView) view.findViewById(R.id.Information);
        }
    }
}
