package RestarantApp.model;

import java.util.ArrayList;

public class GalleryModel {

    String Status_code,Success;
    int tot_vip_gallery;
    ArrayList<VIP_Gallery_list> VIP_Gallery_list = new ArrayList<>();
    ArrayList<Video_Gallery_list> Video_Gallery_list = new ArrayList<>();

    public ArrayList<GalleryModel.Video_Gallery_list> getVideo_Gallery_list() {
        return Video_Gallery_list;
    }

    public void setVideo_Gallery_list(ArrayList<GalleryModel.Video_Gallery_list> video_Gallery_list) {
        Video_Gallery_list = video_Gallery_list;
    }

    public String getStatus_code() {
        return Status_code;
    }

    public void setStatus_code(String status_code) {
        Status_code = status_code;
    }

    public String getSuccess() {
        return Success;
    }

    public void setSuccess(String success) {
        Success = success;
    }

    public int getTot_vip_gallery() {
        return tot_vip_gallery;
    }

    public void setTot_vip_gallery(int tot_vip_gallery) {
        this.tot_vip_gallery = tot_vip_gallery;
    }

    public ArrayList<GalleryModel.VIP_Gallery_list> getVIP_Gallery_list() {
        return VIP_Gallery_list;
    }

    public void setVIP_Gallery_list(ArrayList<GalleryModel.VIP_Gallery_list> VIP_Gallery_list) {
        this.VIP_Gallery_list = VIP_Gallery_list;
    }

    public class VIP_Gallery_list
    {
        String vip_gal_id,title,description,image,date;

        public String getVip_gal_id() {
            return vip_gal_id;
        }

        public void setVip_gal_id(String vip_gal_id) {
            this.vip_gal_id = vip_gal_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }

    public class Video_Gallery_list
    {
        String video_gal_id,title,description,video;

        public String getVideo_gal_id() {
            return video_gal_id;
        }

        public void setVideo_gal_id(String video_gal_id) {
            this.video_gal_id = video_gal_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }
    }
}
