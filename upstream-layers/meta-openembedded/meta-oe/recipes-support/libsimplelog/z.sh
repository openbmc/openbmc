#!/bin/bash
# Tự động dừng script nếu có bất kỳ lệnh nào bị lỗi
set -e

echo "============================================="
echo "Starting automated Git rewrite for V2 submission..."
echo "============================================="

# 1. Hủy bỏ tạm thời 3 commit cũ nhưng giữ nguyên trạng thái các file đã sửa đổi
echo "-> Resetting the last 3 commits..."
git reset --soft HEAD~3

# 2. Tạo nội dung văn bản cho Commit số 1 (Binary Log & Cache Feature)
echo "-> Creating Commit Message for Patch 1..."
cat << 'EOF' > msg_patch1.txt
libsimplelog: add binary log feature and cache hit/miss support

Introduce a new binary log feature based on UTF-8 encoding patterns
to optimize logging capabilities. 

Add cache hit and miss tracking mechanism to monitor performance 
effectiveness.

Signed-off-by: Thuận Nguyễn-Thái <nguyenthaithuanalg@gmail.com>
EOF

# 3. Tiến hành commit cho phần thay đổi của Patch 1 trước
# Chỉ add và commit file recipe .bb gốc tại thời điểm có tính năng này
echo "-> Committing Patch 1..."
git commit -s -F msg_patch1.txt
rm msg_patch1.txt

# 4. Tạo nội dung văn bản cho Commit số 2 (Upgrade 1.1.0 & GNU_SOURCE)
echo "-> Creating Commit Message for Patch 2..."
cat << 'EOF' > msg_patch2.txt
libsimplelog: upgrade 1.0.8 -> 1.1.0

Upgrade libsimplelog to the latest upstream version 1.1.0.

Drop 0001-allow-build-with-cmake-4.patch as it is now integrated
upstream (commit 4827c4325063266f6d2a7e133a9d3a9050ff6a3c).

Update LIC_FILES_CHKSUM due to upstream updating the copyright year 
range from 2024 to 2024-2026 in LICENSE.txt. The project remains fully 
under the MIT license.

Append -D__LINUX__=1 and -D_GNU_SOURCE=1 to EXTRA_OECMAKE to enable 
Linux and GNU-specific extensions like sched_getcpu() for better 
performance effectiveness. These flags are optional; without them, 
version 1.1.0 remains fully backward compatible and adapts perfectly 
with strictly POSIX musl environments.

Signed-off-by: Thuận Nguyễn-Thái <nguyenthaithuanalg@gmail.com>
EOF

# 5. Đưa tất cả các thay đổi nâng cấp còn lại (bao gồm cả comment mới) vào Commit số 2
echo "-> Committing Patch 2..."
git add .
git commit -s -F msg_patch2.txt
rm msg_patch2.txt

# 6. Xuất chuỗi bản vá phiên bản V2 (2 file patch)
echo "-> Generating V2 patches..."
git format-patch -2 -v2

echo "============================================="
echo "SUCCESS! Your local history is cleaned up."
echo "V2 Patches are generated in the current directory."
echo "============================================="
ls -l v2-*.patch

