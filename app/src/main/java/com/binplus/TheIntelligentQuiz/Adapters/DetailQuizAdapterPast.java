package com.binplus.TheIntelligentQuiz.Adapters;


import static com.binplus.TheIntelligentQuiz.BaseURL.BaseURL.BASE_URL_IMAGE;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binplus.TheIntelligentQuiz.Fragments.QuestionsFragment;

import com.binplus.TheIntelligentQuiz.Model.QuizDetailModel;
import com.binplus.TheIntelligentQuiz.R;
import com.squareup.picasso.Picasso;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class DetailQuizAdapterPast extends RecyclerView.Adapter<DetailQuizAdapterPast.QuizViewHolder> {

    private List<QuizDetailModel.Datum> quizList;
    private FragmentManager fragmentManager;

    public DetailQuizAdapterPast(List<QuizDetailModel.Datum> quizModelList,FragmentManager fragmentManager) {
        this.quizList = quizModelList;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail_quiz_past, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizDetailModel.Datum quizModel = quizList.get(position);
        holder.quizTitle.setText(quizModel.getName());
        String image = quizModel.getImage();
        if (image != null && !image.isEmpty()) {
            Picasso.get()
                    .load(BASE_URL_IMAGE + image)
                    .placeholder(R.drawable.ic_piggy_bank)
                    .error(R.drawable.ic_piggy_bank)
                    .into(holder.quizImage);
        } else {
            holder.quizImage.setImageResource(R.drawable.ic_piggy_bank);
        }
        holder.availableSpots.setText(quizModel.getAvailable_spot());
        holder.prizePoolDetail.setText("Rs." + quizModel.getPrize_pool() + "/-");
        holder.joiningFees.setText("Rs." + quizModel.getEntry() + "/-");
        holder.quizTime.setText(quizModel.getStart_date());
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        private ImageView quizImage;
        private TextView quizTitle, quizTime, joiningFees, availableSpots, prizePoolDetail;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizImage = itemView.findViewById(R.id.quiz_image);
            quizTitle = itemView.findViewById(R.id.quiz_title);
            quizTime = itemView.findViewById(R.id.quiz_time);
            joiningFees = itemView.findViewById(R.id.joining_fees);
            availableSpots = itemView.findViewById(R.id.available_spots);
            prizePoolDetail = itemView.findViewById(R.id.prize_pool);
        }
    }
}
