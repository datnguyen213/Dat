package com.example.bai4;
import retrofit2.Call;
import retrofit2.http.GET;
import java.util.List;

public interface ApiService {
    @GET("posts")
    Call<List<Post>> getPosts();  // Gọi tới: https://jsonplaceholder.typicode.com/posts
}
