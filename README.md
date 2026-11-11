# 🐻 Game Học Bảng Cửu Chương

**Game Học Bảng Cửu Chương** là ứng dụng Android giáo dục tương tác sinh động giúp học sinh tiểu học ôn luyện và ghi nhớ bảng cửu chương (từ 2 đến 9) một cách dễ dàng và hứng thú thông qua **4 mini-game độc đáo**, hệ thống **nhiệm vụ hằng ngày/tuần** và **bảng thành tích thăng cấp**.

---

## 🌟 Tính Năng Nổi Bật

### 👤 Quản Lý Người Chơi (Player Profile)
- **Khởi đầu thân thiện:** Màn hình Bắt đầu cho phép nhập tên người chơi để cá nhân hóa trải nghiệm.
- **Đồng bộ dữ liệu:** Điểm cao nhất (`high_score`) và tổng điểm KN (`total_score`) được lưu trữ và cập nhật tự động theo tên từng người chơi trong CSDL SQLite.

### 🎮 4 Mini-Game Học Tập Hấp Dẫn
1. 🔨 **Đập Chuột (Whack-a-Mole):** Phản ứng nhanh mắt nhanh tay, quan sát phép toán hiển thị và đập trúng chú chuột mang đáp án chính xác trong 6 hang chuột.
2. 🎈 **Hứng Bóng (Catch Ball):** Di chuyển giỏ hứng sang Trái/Phải để hứng quả bóng chứa kết quả đúng khi bóng đang rơi xuống.
3. 🏎️ **Đua Xe (Car Racing):** Trả lời đúng phép tính để điều khiển xe đua lách qua vật cản và vọt thẳng về đích.
4. 🚣 **Chèo Thuyền (Boat Rowing):** Lội ngược dòng sông, chọn đúng kết quả để chèo thuyền vượt qua chướng ngại vật an toàn.

---

### 📜 Hệ Thống Nhiệm Vụ (Missions)
- **Nhiệm vụ Hôm nay & Tuần này:** Cung cấp danh sách thử thách tương ứng cho từng loại trò chơi.
- **Rương Phần Thưởng:** Tích lũy hoàn thành đủ 3 nhiệm vụ để mở Rương kho báu nhận thêm **+50 KN** (Điểm kinh nghiệm).
- **Nhận thưởng tự động:** Tự động cộng điểm thưởng KN và cập nhật cấp độ ngay khi nhận thưởng.

---

### 🏆 Cấp Độ & Bảng Thành Tích (Levels & Achievements)
- **Cấp độ người chơi (Level):** Tích lũy điểm KN qua từng trận đấu để thăng cấp (Cấp 1 cần 200 KN, mỗi cấp sau tăng thêm 100 KN).
- **Thống kê chi tiết:** Hiển thị tổng số câu trả lời đúng tích lũy từ SQLite Database cho từng mini-game.
- **Hệ thống Danh hiệu (Badges):** Mở khóa các huy hiệu danh giá khi hoàn thành thử thách đặc biệt:
  1. 🐭 **Dũng sĩ diệt chuột:** Đập trúng 100 chú chuột.
  2. 🎈 **Bàn tay khéo léo:** Hứng được 100 quả bóng.
  3. 🏎️ **Tay lái cừ khôi:** Vượt qua 120 chặng đua.
  4. 👑 **Vua đập chuột:** Đạt từ 500 điểm trở lên trong game Đập chuột.
  5. ⭐ **Kỷ lục hứng bóng:** Đạt lượt chơi hoàn hảo (không bỏ sót quả nào) trong game Hứng bóng.

---

### 💡 Trải Nghiệm Người Dùng (UI/UX)
- ⏱️ **Bộ đếm ngược thời gian (`GameTimer`):** Đếm ngược sinh động từng lượt chơi và tích lũy thời gian chơi thực tế.
- 🔥 **Hệ thống Combo (`comboCount`):** Thưởng thêm điểm kinh nghiệm khi trả lời đúng liên tiếp.
- ❤️ **Mạng sống (Lives):** 3 Trái tim cho mỗi lượt chơi.
- ⏸️ **Custom Dialogs:** Giao diện Dialog **Tạm Dừng** và **Chọn Sai** bo góc hoạt hình thân thiện với trẻ em.

---

## 🛠️ Công Nghệ & Kiến Trúc Sử Dụng

- **Ngôn ngữ phát triển:** Java (Android Native)
- **Kiến trúc ứng dụng:** Single Activity Architecture (`MainActivity` điều hướng ViewGroup `container`)
- **Thiết kế hướng đối tượng (OOP):** Lớp trừu tượng `BaseGame` định nghĩa toàn bộ khung xương (Framework) dùng chung cho các game con kế thừa.
- **Cơ sở dữ liệu:** SQLite (`DatabaseHelper`) lưu trữ thông tin Người chơi (`players`), Lịch sử kết quả (`game_results`), và Tiến độ nhiệm vụ (`missions`).
- **Lưu trữ cấu hình:** `SharedPreferences` (`GAME_DATA`) lưu điểm tổng KN và điểm cao nhất.
- **Giao diện (UI):** Custom Layouts, `ConstraintLayout`, `RecyclerView` (`MissionAdapter`), Custom Dialogs.
- **Hoạt ảnh (Animation):** `ObjectAnimator`, `AnimatorSet`, `RotateAnimation`, `CountDownTimer`.
- **Yêu cầu hệ thống:**
  - `minSdkVersion`: 24 (Android 7.0 Nougat trở lên)
  - `targetSdkVersion`: 37

---

## 📂 Cấu Trúc Thư Mục Dự Án

```
com.example.gamehocbangcuuchuong
├── MainActivity.java            # Activity duy nhất quản lý điều hướng và chuyển đổi màn hình
├── adapter
│   └── MissionAdapter.java      # Adapter hiển thị danh sách nhiệm vụ (RecyclerView)
├── data
│   └── DatabaseHelper.java      # Quản lý CSDL SQLite (Player, GameResult, Mission)
├── dialog
│   └── GameDialog.java          # Dialog Tạm dừng và Dialog Báo chọn sai
├── game
│   ├── BaseGame.java            # [Abstract Class] Khung xương & logic dùng chung cho 4 game
│   ├── DapChuotGame.java        # Logic mini-game Đập chuột
│   ├── DuaXeGame.java          # Logic mini-game Đua xe
│   ├── HungBongGame.java        # Logic mini-game Hứng bóng
│   └── CheoThuyenGame.java      # Logic mini-game Chèo thuyền
├── manager
│   ├── GameManager.java         # Quản lý khởi tạo và dừng các luồng mini-game
│   ├── NhiemVuManager.java      # Quản lý giao diện & logic màn Nhiệm vụ
│   └── ThanhTichManager.java    # Quản lý giao diện & tính toán Cấp độ/Thành tích
├── model
│   ├── GameResult.java          # Model dữ liệu kết quả lượt chơi
│   ├── Mission.java             # Model dữ liệu nhiệm vụ
│   ├── Player.java              # Model dữ liệu người chơi
│   └── Question.java            # Model dữ liệu câu hỏi cửu chương
└── utils
    ├── GameTimer.java           # Bộ đếm ngược lượt chơi & tích lũy thời gian chơi
    ├── LivesManager.java        # Quản lý hiển thị mạng sống (Trái tim)
    ├── QuestionGenerator.java   # Thuật toán sinh tự động câu hỏi phép tính & đáp án gây nhiễu
    └── ScoreManager.java        # Quản lý điểm số, combo, công thức cấp độ & SharedPreferences
```

---

## 🚀 Hướng Dẫn Cài Đặt & Khởi Chạy

1. **Tải/Clone dự án:**
   Mở dự án `GameHocBangCuuChuong` trong **Android Studio**.

2. **Đồng bộ Gradle:**
   Nhấn **Sync Project with Gradle Files** để đảm bảo nạp đủ dependencies.

3. **Chạy ứng dụng:**
   - Kết nối thiết bị Android thật (đã bật *USB Debugging*) hoặc mở Android Emulator.
   - Bấm nút **Run 'app'** (`Shift + F10`) để biên dịch và trải nghiệm trò chơi.
