package com.pvi.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pvi.activities.R;
import com.pvi.objects.LossHistoryObject;
import com.pvi.objects.PVICarObject;

import java.util.List;

/**
 * Created by tuyenpt on 24/05/2016.
 */
public class RestrictionAdapter extends ArrayAdapter {
    private Activity context;
    private List<PVICarObject> dataSource;
    private List<LossHistoryObject> lossDataSource;
    private int resourceID, typeSearch;
    public static final int RESTRICTION_LIST   = 0;
    public static final int LOSS_HISTORY       = 1;

    /**
     * Constructor
     * @param context
     * @param resource
     * @param objects
     */
    public RestrictionAdapter(Activity context, int resource, List<PVICarObject> objects) {
        super(context, resource, objects);
        this.context = context;
        this.dataSource = objects;
        this.resourceID = resource;
        typeSearch = RESTRICTION_LIST;
    }

    public RestrictionAdapter(Activity context, int resource, List<LossHistoryObject> objects, int _typeSearch) {
        super(context, resource, objects);
        this.context = context;
        this.lossDataSource = objects;
        this.resourceID = resource;
        typeSearch = _typeSearch;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        convertView = inflater.inflate(resourceID, null);

        TextView lisence = (TextView) convertView.findViewById(R.id.lisencePlateCar);
        TextView name = (TextView) convertView.findViewById(R.id.customerNameCar);
        TextView serial = (TextView) convertView.findViewById(R.id.serialCar);
        TextView firstDayCar = (TextView) convertView.findViewById(R.id.firstDayCar);
        TextView lastDayCar = (TextView) convertView.findViewById(R.id.lastDayCar);
        TextView ratio = (TextView) convertView.findViewById(R.id.ratioCar);
        TextView groupCar = (TextView) convertView.findViewById(R.id.groupCar);
        TextView note = (TextView) convertView.findViewById(R.id.noteCar);
        TextView blacklist = (TextView) convertView.findViewById(R.id.isBlacklist);

        if (typeSearch == RESTRICTION_LIST) {
            if(this.dataSource.size() > 0 && position >= 0) {
                PVICarObject object = this.dataSource.get(position);
                lisence.setText(object.getLicense());
                name.setText(object.getName());
                serial.setText(object.getSerial());
                firstDayCar.setText(object.getFirstDay());
                lastDayCar.setText(object.getLastDay());
                ratio.setText(object.getRatio());
                groupCar.setText(object.getGroup());
                note.setText(object.getNote());
                blacklist.setVisibility(View.GONE);
            }
        } else if (typeSearch == LOSS_HISTORY) {
            TextView titleGroupCar = (TextView) convertView.findViewById(R.id.titleGroupCar);
            TextView titleNoteField = (TextView) convertView.findViewById(R.id.titleNoteField);
            if (this.lossDataSource.size() > 0 && position >= 0) {
                LossHistoryObject object = this.lossDataSource.get(position);
                lisence.setText(object.getLisencePlate());
                name.setText(object.getName());
                serial.setText(object.getSerial());
                firstDayCar.setText(object.getFirstDay());
                lastDayCar.setText(object.getLastDay());
                ratio.setText(object.getLossRatio() + "%");
                titleGroupCar.setText("Số vụ: ");
                groupCar.setText(object.getNumberOfLoss());
                titleNoteField.setText("Đơn vị: ");
                note.setText(object.getUnit());
                blacklist.setText(object.getBlacklist());
            }
        }
        return convertView;
    }
}
