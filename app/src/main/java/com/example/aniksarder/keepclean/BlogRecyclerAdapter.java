package com.example.aniksarder.keepclean;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.aniksarder.keepclean.UserCommnects.CommentsActivity;
import com.example.aniksarder.keepclean.UserList.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {

    public List<BlogPost> blog_list;
    public List<User> user_list;
    public Context context;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blog_list, List<User> user_list ) {

        this.blog_list = blog_list;
        this.user_list = user_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item, parent, false);
        context = parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final String blogPostId = blog_list.get(position).BlogPostId;
        final String current_user_id = firebaseAuth.getCurrentUser().getUid();

        String desc_data = blog_list.get(position).getDesc();
        holder.setDescText(desc_data);
        

        String place_name = blog_list.get(position).getPlace_name();
        holder.setPlaceNane(place_name);

        String address_name = blog_list.get(position).getAddress();
        holder.setAddressView(address_name);

        String image_url = blog_list.get(position).getImage_url();
        String thumbUri = blog_list.get(position).getImage_thumb();
        holder.setBlogImage(image_url, thumbUri);


        String blog_user_id = blog_list.get(position).getUser_id();

        if (blog_user_id.equals(current_user_id)){

            holder.blogDeleteBtn.setEnabled(true);
            holder.blogDeleteBtn.setVisibility(View.VISIBLE);
        }


        String userName = user_list.get(position).getName();
        String userImage = user_list.get(position).getImage();
        holder.setUserData(userName, userImage);


        try {
            long millisecond = blog_list.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }


/*
        //Get Likes Count
        firebaseFirestore.collection("Posts/" + blogPostId + "/Comments").addSnapshotListener( new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {

                if(!documentSnapshots.isEmpty()){

                    int count = documentSnapshots.size();

                    holder.updateCommentCount(count);

                } else {

                    holder.updateCommentCount(0);

                }

            }
        });
*/
        //comments

        holder.blogCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent commentIntent = new Intent(context, CommentsActivity.class);
                commentIntent.putExtra("blog_post_id", blogPostId);
                context.startActivity(commentIntent);

            }
        });

        holder.blogDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts").document(blogPostId).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        blog_list.remove(position);
                        user_list.remove(position);
                        notifyItemChanged(position);
                        notifyItemRangeChanged(position,blog_list.size());
                       // notifyItemRangeChanged(position,user_list.size());
                    }
                });
            }
        });



    }

    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;

        private TextView descView;
        private TextView placeNane;
        private TextView addressView;
        private ImageView blogImageView;
        private TextView blogDate;
        private Button blogDeleteBtn;
        private TextView blogUserName;
        private CircleImageView blogUserImage;


        private RelativeLayout blogCommentBtn;
        //private TextView blogCommentCount;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            blogDeleteBtn = mView.findViewById(R.id.detete_blog_btn);
            blogCommentBtn = mView.findViewById(R.id.rl_blog_comment);

        }


        public void setDescText(String descText) {
            descView = mView.findViewById(R.id.blog_desc);
            descView.setText(descText);

        }

        public void setPlaceNane(String placeText) {
            placeNane = mView.findViewById(R.id.blog_place);
            placeNane.setText(placeText);
        }


        public void setAddressView(String addressText) {
            addressView = mView.findViewById(R.id.blog_address);
            addressView.setText(addressText);

        }

        public void setBlogImage(String downloadUri , String thumbUri) {

            blogImageView = mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.add_btn);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).thumbnail
                    (Glide.with(context).load(thumbUri)).into(blogImageView);

        }

        public void setTime(String date) {

            blogDate = mView.findViewById(R.id.blog_date);
            blogDate.setText(date);

        }


        public void setUserData(String name, String image) {

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.ic_lens_black_24dp);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

        }

/*
        public void updateCommentCount(int count){

            blogCommentCount = mView.findViewById(R.id.blog_comment_count);
            blogCommentCount.setText(count + " Commnets");

        }
*/

    }

}
