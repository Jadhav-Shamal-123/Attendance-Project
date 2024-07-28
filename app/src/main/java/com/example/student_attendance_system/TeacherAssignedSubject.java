package com.example.student_attendance_system;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class TeacherAssignedSubject extends RecyclerView.Adapter<TeacherAssignedSubject.ViewHolder> {
    private LayoutInflater inflater;
    private List<TeachersubjectassignModel> teachersubjectassignModels;
    private Context context;

    public TeacherAssignedSubject(List<TeachersubjectassignModel> teachersubjectassignModels, Context context) {
        this.teachersubjectassignModels = teachersubjectassignModels;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.teachersubject, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TeachersubjectassignModel model = teachersubjectassignModels.get(position);
        if (model!=null){
        // Bind data to TextViews
            try {
                holder.teacherName.setText(model.getTeacherName());
                holder.course.setText(model.getCourse());
                holder.year.setText(model.getYear());
                holder.subjectName.setText(model.getSubjectName());
                holder.subjectCode.setText(model.getSubjectCode());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        return teachersubjectassignModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView teacherName, course, year, subjectName, subjectCode;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            teacherName = itemView.findViewById(R.id.teachername);
            course = itemView.findViewById(R.id.course);
            year = itemView.findViewById(R.id.year);
            subjectName = itemView.findViewById(R.id.subjectname);
            subjectCode = itemView.findViewById(R.id.subjectcode);
        }
    }
}

