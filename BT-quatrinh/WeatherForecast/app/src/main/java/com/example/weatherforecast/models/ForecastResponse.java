package com.example.weatherforecast.models;

import java.util.List;

public class ForecastResponse {

    // Danh sách dự báo thời tiết theo giờ (cách nhau 3 giờ)
    public List<ForecastItem> list;

    public List<ForecastItem> getList() {
        return list;
    }

    public void setList(List<ForecastItem> list) {
        this.list = list;
    }

    public static class ForecastItem {
        // Thời gian dạng Unix timestamp (giây)
        public long dt;

        // Thông tin chính về nhiệt độ, áp suất, độ ẩm...
        public Main main;

        // Danh sách thông tin thời tiết (trời mây, nắng, mưa,...)
        public List<Weather> weather;

        // Mức độ mây (cloudiness) tính theo %
        public Clouds clouds;

        // Thông tin gió
        public Wind wind;

        // Tầm nhìn (tính bằng mét), ví dụ: 10000 = 10km
        public int visibility;

        // Xác suất mưa (%), giá trị từ 0 -> 1
        public double pop;

        // Thông tin phụ thêm (ví dụ ban ngày/ban đêm)
        public Sys sys;

        // Thời gian dạng chuỗi, ví dụ: "2025-04-17 13:00:00"
        public String dt_txt;

        public long getDt() {
            return dt;
        }

        public void setDt(long dt) {
            this.dt = dt;
        }

        public Main getMain() {
            return main;
        }

        public void setMain(Main main) {
            this.main = main;
        }

        public List<Weather> getWeather() {
            return weather;
        }

        public void setWeather(List<Weather> weather) {
            this.weather = weather;
        }

        public Clouds getClouds() {
            return clouds;
        }

        public void setClouds(Clouds clouds) {
            this.clouds = clouds;
        }

        public Wind getWind() {
            return wind;
        }

        public void setWind(Wind wind) {
            this.wind = wind;
        }

        public int getVisibility() {
            return visibility;
        }

        public void setVisibility(int visibility) {
            this.visibility = visibility;
        }

        public double getPop() {
            return pop;
        }

        public void setPop(double pop) {
            this.pop = pop;
        }

        public Sys getSys() {
            return sys;
        }

        public void setSys(Sys sys) {
            this.sys = sys;
        }

        public String getDt_txt() {
            return dt_txt;
        }

        public void setDt_txt(String dt_txt) {
            this.dt_txt = dt_txt;
        }

        // ---------- Các class con ------------

        public static class Main {
            // Nhiệt độ hiện tại (Kelvin, trừ khi có đơn vị khác như metric = °C)
            public double temp;

            // Cảm giác nhiệt (nhiệt độ cảm nhận)
            public double feels_like;

            // Nhiệt độ tối thiểu
            public double temp_min;

            // Nhiệt độ tối đa
            public double temp_max;

            // Áp suất khí quyển tại mặt đất (hPa)
            public int pressure;

            // Áp suất mực nước biển (nếu có)
            public int sea_level;

            // Áp suất mặt đất (ground level)
            public int grnd_level;

            public double getTemp() {
                return temp;
            }

            public void setTemp(double temp) {
                this.temp = temp;
            }

            public double getFeels_like() {
                return feels_like;
            }

            public void setFeels_like(double feels_like) {
                this.feels_like = feels_like;
            }

            public double getTemp_min() {
                return temp_min;
            }

            public void setTemp_min(double temp_min) {
                this.temp_min = temp_min;
            }

            public double getTemp_max() {
                return temp_max;
            }

            public void setTemp_max(double temp_max) {
                this.temp_max = temp_max;
            }

            public int getPressure() {
                return pressure;
            }

            public void setPressure(int pressure) {
                this.pressure = pressure;
            }

            public int getSea_level() {
                return sea_level;
            }

            public void setSea_level(int sea_level) {
                this.sea_level = sea_level;
            }

            public int getGrnd_level() {
                return grnd_level;
            }

            public void setGrnd_level(int grnd_level) {
                this.grnd_level = grnd_level;
            }

            public int getHumidity() {
                return humidity;
            }

            public void setHumidity(int humidity) {
                this.humidity = humidity;
            }

            public double getTemp_kf() {
                return temp_kf;
            }

            public void setTemp_kf(double temp_kf) {
                this.temp_kf = temp_kf;
            }

            // Độ ẩm không khí (%)
            public int humidity;

            // Nhiệt độ thay đổi nội bộ (có thể bỏ qua)
            public double temp_kf;
        }

        public static class Weather {
            // Mã thời tiết (dùng để xử lý icon tùy chỉnh)
            public int id;

            // Tên loại thời tiết (Clear, Clouds, Rain,...)
            public String main;

            // Mô tả chi tiết thời tiết (ví dụ: "light rain")
            public String description;

            // Mã icon của thời tiết, dùng để load ảnh từ API
            public String icon;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getMain() {
                return main;
            }

            public void setMain(String main) {
                this.main = main;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public String getIcon() {
                return icon;
            }

            public void setIcon(String icon) {
                this.icon = icon;
            }
        }

        public static class Clouds {
            // Phần trăm độ phủ của mây
            public int all;

            public int getAll() {
                return all;
            }

            public void setAll(int all) {
                this.all = all;
            }
        }

        public static class Wind {
            // Tốc độ gió (m/s hoặc theo đơn vị yêu cầu)
            public double speed;

            // Hướng gió (0 - 360 độ)
            public int deg;

            // Gió giật (nếu có)
            public double gust;

            public double getSpeed() {
                return speed;
            }

            public void setSpeed(double speed) {
                this.speed = speed;
            }

            public int getDeg() {
                return deg;
            }

            public void setDeg(int deg) {
                this.deg = deg;
            }

            public double getGust() {
                return gust;
            }

            public void setGust(double gust) {
                this.gust = gust;
            }
        }

        public static class Sys {
            // Ban ngày ("d") hoặc ban đêm ("n")
            public String pod;

            public String getPod() {
                return pod;
            }

            public void setPod(String pod) {
                this.pod = pod;
            }
        }
    }
}