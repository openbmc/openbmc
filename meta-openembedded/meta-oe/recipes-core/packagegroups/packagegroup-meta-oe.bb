SUMMARY = "Meta-oe ptest packagegroups"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "\
    packagegroup-meta-oe \
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-connectivity \
    packagegroup-meta-oe-connectivity-python2 \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-dbs \
    packagegroup-meta-oe-dbs-python2 \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    packagegroup-meta-oe-extended-python2 \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-printing \
    packagegroup-meta-oe-shells \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-support \
    packagegroup-meta-oe-support-python2 \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
"
#PACKAGES += "packagegroup-meta-oe-fortran-packages"

RDEPENDS:packagegroup-meta-oe = "\
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-connectivity \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-connectivity-python2", "", d)} \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-dbs \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-dbs-python2", "", d)} \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-extended-python2", "", d)} \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-printing \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-shells \
    packagegroup-meta-oe-support \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "packagegroup-meta-oe-support-python2", "", d)} \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
"

RDEPENDS:packagegroup-meta-oe-benchmarks = "\
    bonnie++ \
    dbench \
    dhrystone \
    fio \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "glmark2", "", d)} \
    iozone3 \
    iperf2 \
    iperf3 \
    libc-bench \
    linpack \
    lmbench \
    mbw \
    memtester \
    nbench-byte \
    phoronix-test-suite \
    qperf \
    s-suite \
    stressapptest \
    tinymembench \
    tiobench \
    whetstone \
"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:armv7a = " cpuburn-arm sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:armv7ve = " cpuburn-arm sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:aarch64 = " cpuburn-arm sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:x86 = " sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:x86-64 = " sysbench"
RDEPENDS:packagegroup-meta-oe-benchmarks:append:mips = " sysbench"

RDEPENDS:packagegroup-meta-oe-benchmarks:remove:mipsarch = "libhugetlbfs"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:mips64 = "tinymembench"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:mips64el = "tinymembench"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:riscv64 = "libhugetlbfs"
RDEPENDS:packagegroup-meta-oe-benchmarks:remove:riscv32 = "libhugetlbfs"

RDEPENDS:packagegroup-meta-oe-bsp ="\
    acpitool \
    cpufrequtils \
    edac-utils \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "firmwared", "", d)} \
    flashrom \
    fwupd \
    fwupd-efi \
    irda-utils \
    lmsensors-config-cgi \
    lmsensors-config-fancontrol \
    lmsensors-config-sensord \
    lmsensors \
    lsscsi \
    nvme-cli \
    pcmciautils \
    pointercal \
"
RDEPENDS:packagegroup-meta-oe-bsp:append:x86 = " ledmon"
RDEPENDS:packagegroup-meta-oe-bsp:append:x86-64 = " ledmon"

RDEPENDS:packagegroup-meta-oe-bsp:remove:libc-musl = "ledmon"
RDEPENDS:packagegroup-meta-oe-bsp:remove:mipsarch = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:powerpc = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:powerpc64 = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:powerpc64le = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:riscv64 = "efivar efibootmgr fwupd fwupd-efi"
RDEPENDS:packagegroup-meta-oe-bsp:remove:riscv32 = "efivar efibootmgr fwupd fwupd-efi"

RDEPENDS:packagegroup-meta-oe-connectivity ="\
    cyrus-sasl \
    gammu \
    gattlib \
    gensio \
    hostapd \
    ifplugd \
    irssi \
    iwd \
    krb5 \
    libev \
    libimobiledevice \
    libmbim \
    libmtp \
    libndp \
    libnet \
    libqmi \
    libtorrent \
    libuv \
    libwebsockets \
    linuxptp \
    loudmouth \
    modemmanager \
    mosh \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "obex-data-server", "", d)} \
    openobex \
    obexftp \
    packagegroup-tools-bluetooth \
    paho-mqtt-c \
    paho-mqtt-cpp \
    rabbitmq-c \
    rfkill \
    rtorrent \
    ser2net \
    smstools3 \
    telepathy-glib \
    thrift \
    usbmuxd \
    wifi-test-suite \
    zabbix \
    czmq \
    zeromq \
"

RDEPENDS:packagegroup-meta-oe-connectivity:append:libc-glibc = " wvstreams wvdial"

RDEPENDS:packagegroup-meta-oe-connectivity-python2 = "\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "telepathy-idle", "", d)} \
"

# dracut needs dracut
RDEPENDS:packagegroup-meta-oe-core = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "dbus-broker", "", d)} \
    dbus-daemon-proxy \
    libdbus-c++ \
    emlog \
    kernel-module-emlog \
    glibmm \
    libnfc \
    libsigc++-2.0 \
    libsigc++-3 \
    libxml++ \
    mdbus2 \
    distro-feed-configs \
    mm-common \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "ndctl", "", d)} \
    pim435 \
    proxy-libintl \
    safec \
    sdbus-c++-tools \
    sdbus-c++ \
    toybox \
    usleep \
    dbus-cxx \
"
RDEPENDS:packagegroup-meta-oe-core:append:libc-glibc = " ${@bb.utils.contains("DISTRO_FEATURES", "x11 opengl", "glfw", "", d)}"
RDEPENDS:packagegroup-meta-oe-core:remove:riscv64 = "safec"
RDEPENDS:packagegroup-meta-oe-core:remove:riscv32 = "safec"

RDEPENDS:packagegroup-meta-oe-crypto ="\
    botan \
    cryptsetup \
    fsverity-utils \
    libkcapi \
    libmcrypt \
    libsodium \
    pkcs11-helper \
"
RDEPENDS:packagegroup-meta-oe-crypto:remove:riscv32 = "botan"

RDEPENDS:packagegroup-meta-oe-dbs ="\
    influxdb \
    leveldb \
    libdbi \
    lmdb \
    mariadb \
    postgresql \
    psqlodbc \
    rocksdb \
    soci \
"
RDEPENDS:packagegroup-meta-oe-dbs:remove:libc-musl:powerpc = "rocksdb"

RDEPENDS:packagegroup-meta-oe-dbs-python2 ="\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", bb.utils.contains('I_SWEAR_TO_MIGRATE_TO_PYTHON3', 'yes', 'mysql-python', '', d), "", d)} \
"

RDEPENDS:packagegroup-meta-oe-devtools ="\
    abseil-cpp \
    apitrace \
    breakpad \
    android-tools-conf \
    android-tools \
    concurrencykit \
    cgdb \
    ctags \
    debootstrap \
    cjson \
    cloc \
    icon-slicer \
    cscope \
    dmalloc \
    ${@bb.utils.contains("PACKAGE_CLASSES", "package_rpm", "dnf-plugin-tui", "", d)} \
    doxygen \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "geany-plugins geany", "", d)} \
    jemalloc \
    lemon \
    flatbuffers \
    heaptrack \
    libparse-yapp-perl \
    libubox \
    ltrace \
    luajit \
    mcpp \
    memstat \
    giflib \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "glade", "", d)} \
    grpc \
    guider \
    php \
    iptraf-ng \
    jq \
    json-spirit \
    serialcheck \
    tclap \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "tk", "", d)} \
    uw-imap \
    jsoncpp \
    jsonrpc \
    yajl \
    yajl \
    kconfig-frontends \
    libgee \
    libsombok3 \
    lshw \
    luaposix \
    capnproto-compiler \
    mpich \
    msgpack-c \
    msgpack-cpp \
    mercurial \
    nodejs \
    openocd \
    pax-utils \
    ipc-run \
    libdbd-mysql-perl \
    libdbi-perl \
    libdev-checklib-perl \
    libio-pty-perl \
    libjson-perl \
    libperlio-gzip-perl \
    ply \
    protobuf-c \
    protobuf \
    pugixml \
    python3-distutils-extra \
    python3-pycups \
    rapidjson \
    squashfs-tools-ng \
    uftrace \
    valijson \
    libxerces-c \
    xerces-c-samples \
    xmlrpc-c \
    yasm \
    json-schema-validator \
    poke \
"
RDEPENDS:packagegroup-meta-oe-devtools:append:x86 = " cpuid msr-tools pahole pmtools"
RDEPENDS:packagegroup-meta-oe-devtools:append:x86-64 = " cpuid msr-tools pahole pcimem pmtools"
RDEPENDS:packagegroup-meta-oe-devtools:append:riscv64 = " pcimem"
RDEPENDS:packagegroup-meta-oe-devtools:append:arm = " pcimem"
RDEPENDS:packagegroup-meta-oe-devtools:append:aarch64 = " pahole pcimem"
RDEPENDS:packagegroup-meta-oe-devtools:append:libc-musl = " musl-nscd"

RDEPENDS:packagegroup-meta-oe-devtools:remove:arm = "concurrencykit"
RDEPENDS:packagegroup-meta-oe-devtools:remove:armv5 = "uftrace nodejs"
RDEPENDS:packagegroup-meta-oe-devtools:remove:mipsarch = "concurrencykit lshw ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:mips64 = "luajit nodejs"
RDEPENDS:packagegroup-meta-oe-devtools:remove:mips64el = "luajit nodejs"
RDEPENDS:packagegroup-meta-oe-devtools:remove:powerpc = "android-tools breakpad lshw luajit uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:powerpc64 = "android-tools breakpad lshw luajit ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:powerpc64le = "android-tools breakpad lshw luajit ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:riscv64 = "breakpad concurrencykit heaptrack lshw ltrace luajit nodejs ply"
RDEPENDS:packagegroup-meta-oe-devtools:remove:riscv32 = "breakpad concurrencykit heaptrack lshw ltrace luajit nodejs ply uftrace"
RDEPENDS:packagegroup-meta-oe-devtools:remove:libc-musl:riscv32 = "php"
RDEPENDS:packagegroup-meta-oe-devtools:remove:aarch64 = "concurrencykit"
RDEPENDS:packagegroup-meta-oe-devtools:remove:x86 = "ply"

RDEPENDS:packagegroup-meta-oe-extended ="\
    bitwise \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "boinc-client", "", d)} \
    brotli \
    byacc \
    cmatrix \
    cmpi-bindings \
    collectd \
    ddrescue \
    dialog \
    duktape \
    enscript \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "gnuplot", "", d)} \
    dlt-daemon \
    docopt.cpp \
    iotop \
    dumb-init \
    konkretcmpi \
    figlet \
    libcec \
    libdivecomputer \
    fluentbit \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "libgxim", "", d)} \
    liblognorm \
    libmodbus \
    haveged \
    hexedit \
    hiredis \
    hplip \
    hwloc \
    libleak \
    libuio \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "libwmf", "", d)} \
    libyang \
    lprng \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "icewm", "", d)} \
    md5deep \
    indent \
    jansson \
    nana \
    nicstat \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "openwsman", "", d)} \
    p7zip \
    isomd5sum \
    jpnevulator \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit", "polkit-group-rule-datetime polkit-group-rule-network polkit", "", d)} \
    rarpd \
    redis \
    libfastjson \
    librelp \
    sblim-cmpi-devel \
    sblim-sfc-common \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "sblim-sfcb ", "", d)} \
    sblim-sfcc \
    libblockdev \
    sgpio \
    smartmontools \
    can-utils \
    canutils \
    libsocketcan \
    libconfig \
    linuxconsole \
    uml-utilities \
    libidn \
    libqb \
    wipe \
    libzip \
    zram \
    libplist \
    libusbmuxd \
    liblockfile \
    liblogging \
    libpwquality \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "libreport", "", d)} \
    libserialport \
    libstatgrab \
    lockfile-progs \
    logwatch \
    mraa \
    ostree \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "pam-plugin-ccreds pam-plugin-ldapdb pam-ssh-agent-auth", "", d)} \
    pegtl \
    libfile-fnmatch-perl \
    rrdtool \
    sanlock \
    scsirastools \
    sedutil \
    libsigrok \
    libsigrokdecode \
    s-nail \
    sigrok-cli \
    snappy \
    tipcutils \
    tiptop \
    tmate \
    tmux \
    triggerhappy \
    upm \
    vlock \
    volume-key \
    wxwidgets \
    zlog \
    zstd \
    zsync-curl \
    redis-plus-plus \
"
RDEPENDS:packagegroup-meta-oe-extended:append:libc-musl = " libexecinfo"
RDEPENDS:packagegroup-meta-oe-extended:append:x86-64 = " pmdk libx86-1"
RDEPENDS:packagegroup-meta-oe-extended:append:x86 = " libx86-1"

RDEPENDS:packagegroup-meta-oe-extended:remove:libc-musl = "sysdig"
RDEPENDS:packagegroup-meta-oe-extended:remove:mipsarch = "upm mraa minifi-cpp tiptop"
RDEPENDS:packagegroup-meta-oe-extended:remove:mips = "sysdig"
RDEPENDS:packagegroup-meta-oe-extended:remove:powerpc = "upm mraa minifi-cpp"
RDEPENDS:packagegroup-meta-oe-extended:remove:powerpc64 = "upm mraa minifi-cpp"
RDEPENDS:packagegroup-meta-oe-extended:remove:powerpc64le = "upm mraa sysdig"
RDEPENDS:packagegroup-meta-oe-extended:remove:riscv64 = "upm libleak mraa sysdig tiptop"
RDEPENDS:packagegroup-meta-oe-extended:remove:riscv32 = "upm libleak mraa sysdig tiptop"

RDEPENDS:packagegroup-meta-oe-extended-python2 ="\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", bb.utils.contains('I_SWEAR_TO_MIGRATE_TO_PYTHON3', 'yes', 'openlmi-tools', '', d), "", d)} \
"

RDEPENDS:packagegroup-meta-oe-gnome ="\
    atkmm \
    gcab \
    gnome-common \
    gmime \
    libjcat \
    gtk+ \
    gtkmm3 \
    gtkmm \
    ${@bb.utils.contains("DISTRO_FEATURES", "gobject-introspection-data", "libpeas", "", d)} \
    pyxdg \
    gnome-theme-adwaita \
"

RDEPENDS:packagegroup-meta-oe-graphics ="\
    cairomm \
    directfb-examples \
    directfb \
    fbgrab \
    dietsplash \
    ${@bb.utils.contains("PACKAGE_CLASSES", "package_rpm", "dnfdragora", "", d)} \
    fontforge \
    fbida \
    feh \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "freeglut", "", d)} \
    ftgl \
    fvwm \
    gtkperf \
    gphoto2 \
    imlib2 \
    libgphoto2 \
    graphviz \
    gtkwave \
    jasper \
    libforms \
    lxdm \
    numlockx \
    nyancat \
    obconf \
    openbox \
    packagegroup-fonts-truetype \
    qrencode \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "st", "", d)} \
    leptonica \
    libvncserver \
    libmng \
    libsdl-gfx \
    libsdl-image \
    libsdl-mixer \
    libsdl-net \
    libsdl-ttf \
    libsdl2-image \
    libsdl2-mixer \
    libsdl2-net \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "libsdl2-ttf", "", d)} \
    libsdl \
    ${@bb.utils.contains("DISTRO_FEATURES", "wayland", "lv-drivers lvgl lv-lib-png", "", d)} \
    ttf-arphic-uming \
    ttf-droid-sans ttf-droid-sans-mono ttf-droid-sans-fallback ttf-droid-sans-japanese ttf-droid-serif \
    ttf-abyssinica \
    source-han-sans-cn-fonts \
    source-han-sans-jp-fonts \
    source-han-sans-kr-fonts \
    source-han-sans-tw-fonts \
    source-code-pro-fonts \
    ttf-dejavu-sans \
    ttf-dejavu-sans-condensed \
    ttf-dejavu-sans-mono \
    ttf-dejavu-serif \
    ttf-dejavu-serif-condensed \
    ttf-dejavu-mathtexgyre \
    ttf-dejavu-common \
    ttf-gentium \
    ttf-hunky-sans \
    ttf-hunky-serif \
    ttf-ipag ttf-ipagp ttf-ipamp ttf-ipam \
    ttf-lohit \
    ttf-inconsolata \
    ttf-liberation-sans-narrow \
    ttf-liberation-mono \
    ttf-liberation-sans \
    ttf-liberation-serif \
    ttf-lklug \
    ttf-noto-emoji-color \
    ttf-noto-emoji-regular \
    ttf-sazanami-gothic \
    ttf-sazanami-mincho \
    ttf-takao-pgothic ttf-takao-gothic ttf-takao-pmincho ttf-takao-mincho \
    ttf-tlwg \
    ttf-roboto \
    ttf-wqy-zenhei \
    ttf-pt-sans \
    ttf-vlgothic \
    ttf-ubuntu-mono \
    ttf-ubuntu-sans \
    libyui-ncurses \
    libyui \
    x11vnc \
    terminus-font-consolefonts \
    terminus-font-pcf \
    xdotool \
    xkbevd \
    bdftopcf \
    iceauth \
    sessreg \
    xgamma \
    setxkbmap \
    xkbutils \
    twm \
    xclock \
    xfontsel \
    xkbprint \
    xlsatoms \
    xlsclients \
    xlsfonts \
    xmag \
    xmessage \
    xrdb \
    xrefresh \
    xsetroot \
    xstdcmap \
    xterm \
    xwd \
    xwud \
    xorg-sgml-doctools \
    xf86-input-tslib \
    xf86-input-void \
    xf86-video-armsoc \
    xf86-video-ati \
    font-adobe-100dpi \
    font-adobe-utopia-100dpi \
    font-bh-100dpi \
    font-bh-lucidatypewriter-100dpi \
    font-bitstream-100dpi \
    font-cursor-misc \
    font-misc-misc \
    xorg-fonts-100dpi \
    libxaw6 \
    libxpresent \
    xcb-util-cursor \
    xserver-common \
    openjpeg \
    pangomm \
    spirv-shader-generator \
    spirv-tools \
    stalonetray \
    surf \
    tesseract-lang \
    tesseract \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 pam", "tigervnc", "", d)} \
    tslib \
    unclutter-xfixes \
    libvdpau \
    xcursorgen \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 pam", "xscreensaver", "", d)} \
    yad \
    parallel-deqp-runner \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "opengl-es-cts", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl vulkan", "vulkan-cts", "", d)} \
"
RDEPENDS:packagegroup-meta-oe-graphics:append:x86 = " renderdoc xf86-video-nouveau xf86-video-mga"
RDEPENDS:packagegroup-meta-oe-graphics:append:x86-64 = " renderdoc xf86-video-nouveau xf86-video-mga"
RDEPENDS:packagegroup-meta-oe-graphics:append:arm = " renderdoc"
RDEPENDS:packagegroup-meta-oe-graphics:append:aarch64 = " renderdoc"

RDEPENDS:packagegroup-meta-oe-graphics:remove:libc-musl = "renderdoc"

RDEPENDS:packagegroup-meta-oe-kernel ="\
    agent-proxy \
    crash \
    cpupower \
    ipmitool \
    broadcom-bt-firmware \
    kernel-selftest \
    minicoredumper \
    oprofile \
    spidev-test \
    trace-cmd \
    usbip-tools \
"
RDEPENDS:packagegroup-meta-oe-kernel:append:x86 = " intel-speed-select ipmiutil pm-graph turbostat"
RDEPENDS:packagegroup-meta-oe-kernel:append:x86-64 = " intel-speed-select ipmiutil pm-graph turbostat bpftool"
RDEPENDS:packagegroup-meta-oe-kernel:append:x86-64:libc-glibc = " kpatch"
RDEPENDS:packagegroup-meta-oe-kernel:append:powerpc64 = " libpfm4"

# Kernel-selftest does not build with 5.8 and its exluded from build too so until its fixed remove it
RDEPENDS:packagegroup-meta-oe-kernel:remove = "kernel-selftest"
RDEPENDS:packagegroup-meta-oe-kernel:remove:libc-musl = "bpftool crash intel-speed-select kernel-selftest minicoredumper turbostat"

RDEPENDS:packagegroup-meta-oe-kernel:remove:mipsarcho32 = "makedumpfile"
RDEPENDS:packagegroup-meta-oe-kernel:remove:mips64 = "crash"
RDEPENDS:packagegroup-meta-oe-kernel:remove:mips64el = "crash"

RDEPENDS:packagegroup-meta-oe-kernel:remove:riscv64 = "crash oprofile"
RDEPENDS:packagegroup-meta-oe-kernel:remove:riscv32 = "crash makedumpfile oprofile"

RDEPENDS:packagegroup-meta-oe-multimedia ="\
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "faad2", "", d)} \
    dirsplit \
    genisoimage \
    icedax \
    wodim \
    id3lib \
    audiofile \
    a2jmidid \
    jack-server \
    jack-utils \
    libass \
    libcdio-paranoia \
    libcdio \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "libmad", "", d)} \
    libmms \
    libdvdread \
    libopus \
    live555-examples \
    live555-mediaserver \
    libmikmod \
    libmodplug \
    sound-theme-freedesktop \
    yavta \
    v4l-utils \
    wavpack \
    libvpx \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "xsp", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_ACCEPTED", "commercial", "mpv", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "pavucontrol", "", d)} \
    libopusenc \
"

RDEPENDS:packagegroup-meta-oe-navigation ="\
    geos \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluz4", "gpsd-machine-conf gpsd", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "orrery", "", d)} \
    geoclue \
    libspatialite \
    proj \
"

RDEPENDS:packagegroup-meta-oe-printing ="\
    cups-filters \
    gutenprint \
    qpdf \
"

RDEPENDS:packagegroup-meta-oe-security ="\
    keyutils \
    nmap \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "passwdqc", "", d)} \
    softhsm \
    tomoyo-tools \
    auditd \
"

RDEPENDS:packagegroup-meta-oe-shells ="\
    dash \
    mksh \
    tcsh \
    zsh \
"

RDEPENDS:packagegroup-meta-oe-support ="\
    anthy \
    atop \
    ace-cloud-editor \
    ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "driverctl", "", d)} \
    frame \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "geis", "", d)} \
    grail \
    asio \
    augeas \
    avro-c \
    bdwgc \
    c-ares \
    cmark \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit gobject-introspection-data", "colord", "", d)} \
    consolation \
    cpprest \
    ctapi-common \
    dfu-util \
    dhex \
    digitemp \
    dool \
    espeak \
    evemu-tools \
    exiv2 \
    libnice \
    c-periphery \
    fmt \
    function2 \
    gd \
    gflags \
    glog \
    gperftools \
    gpm \
    gsoap \
    hdf5 \
    hstr \
    htop \
    hunspell-dictionaries \
    hunspell \
    hwdata \
    iksemel \
    gengetopt \
    googlebenchmark \
    imagemagick \
    iniparser \
    inotify-tools \
    joe \
    lcms \
    lcov \
    imapfilter \
    libbytesize \
    libcyusbserial \
    libestr \
    libfann \
    libftdi \
    libjs-jquery-globalize \
    libjs-jquery-cookie \
    ccid \
    zchunk \
    libgpiod \
    libmanette \
    ckermit \
    libcereal \
    daemontools \
    libiio \
    devmem2 \
    libgit2 \
    libharu \
    eject \
    enca \
    epeg \
    libmxml \
    fbset-modes \
    fbset \
    liboop \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "fltk", "", d)} \
    freerdp \
    libgusb \
    emacs \
    libp11 \
    libraw1394 \
    gradm \
    gsl \
    librsync \
    hddtemp \
    hidapi \
    libsoc \
    libmimetic \
    libinih \
    libtar \
    libteam \
    libusb-compat \
    libatasmart \
    libcanberra \
    libssh \
    libssh2 \
    libee \
    libusbgx-config \
    libusbgx \
    lockdev \
    logwarn \
    libjs-sizzle \
    liblinebreak \
    mailcap \
    liboauth \
    mg \
    monit \
    mscgen \
    libsmi \
    remmina \
    neon \
    nmon \
    libjs-jquery-icheck \
    libtinyxml \
    libusbg \
    libutempter \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "links-x11", "links", d)} \
    log4c \
    log4cpp \
    nspr \
    libdevmapper \
    lvm2 \
    nss \
    mbuffer \
    onig \
    mime-support \
    minini \
    multipath-tools \
    numactl \
    clinfo \
    opencv \
    opensc \
    openct \
    openldap \
    wbxml2 \
    p910nd \
    libtinyxml2 \
    picocom \
    funyahoo-plusplus \
    icyque \
    libotr \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "pidgin-otr", "", d)} \
    pidgin \
    purple-skypeweb \
    pidgin-sipe \
    pngcheck \
    poco \
    poppler-data \
    poppler \
    portaudio-v19 \
    procmail \
    pxaregs \
    pv \
    rsnapshot \
    pps-tools \
    raptor2 \
    rdfind \
    re2 \
    sdmon \
    sdparm \
    serial-forward \
    read-edid \
    spitools \
    libsass \
    sassc \
    smarty \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "synergy", "", d)} \
    syslog-ng \
    system-config-keyboard \
    tbb \
    satyr \
    pcp \
    pcsc-lite \
    pcsc-tools \
    sharutils \
    sg3-utils \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "uim", "", d)} \
    uchardet \
    srecord \
    ssiapi \
    tree \
    utouch-evemu \
    utouch-frame \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "utouch-mtview", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "polkit", "udisks2", "", d)} \
    stm32flash \
    tokyocabinet \
    xmlstarlet \
    thin-provisioning-tools \
    uhubctl \
    zile \
    unixodbc \
    daemonize \
    upower \
    unicode-ucd \
    xdelta3 \
    uriparser \
    nano \
    xdg-user-dirs \
    xmlsec1 \
    usb-modeswitch-data \
    usb-modeswitch \
    liburing \
    zbar \
    libmicrohttpd \
    yaml-cpp \
"
RDEPENDS:packagegroup-meta-oe-support:append:armv7a = "${@bb.utils.contains("TUNE_FEATURES","neon"," ne10","",d)}"
RDEPENDS:packagegroup-meta-oe-support:append:armv7ve = "${@bb.utils.contains("TUNE_FEATURES","neon"," ne10","",d)}"
RDEPENDS:packagegroup-meta-oe-support:append:aarch64 = " ne10"
RDEPENDS:packagegroup-meta-oe-support:append:x86 = " mcelog mce-inject mce-test vboxguestdrivers"
RDEPENDS:packagegroup-meta-oe-support:append:x86-64 = " mcelog mce-inject mce-test vboxguestdrivers"

RDEPENDS:packagegroup-meta-oe-support-python2 ="\
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", bb.utils.contains('I_SWEAR_TO_MIGRATE_TO_PYTHON3', 'yes', 'lio-utils', '', d), "", d)} \
"

RDEPENDS:packagegroup-meta-oe-support:remove:arm ="numactl"
RDEPENDS:packagegroup-meta-oe-support:remove:mipsarch = "gperftools"
RDEPENDS:packagegroup-meta-oe-support:remove:riscv64 = "gperftools uim"
RDEPENDS:packagegroup-meta-oe-support:remove:riscv32 = "gperftools uim"
RDEPENDS:packagegroup-meta-oe-support:remove:powerpc = "libcereal ssiapi tbb"
RDEPENDS:packagegroup-meta-oe-support:remove:powerpc64le = "libcereal ssiapi"
RDEPENDS:packagegroup-meta-oe-support:remove:libc-musl = "pcp"
RDEPENDS:packagegroup-meta-oe-support:remove:libc-musl:powerpc = "gsl"

RDEPENDS:packagegroup-meta-oe-test ="\
    bats \
    cmocka \
    cppunit \
    cpputest \
    cukinia \
    cunit \
    cxxtest \
    evtest \
    fb-test \
    fwts \
    googletest \
    pm-qa \
    testfloat \
"
RDEPENDS:packagegroup-meta-oe-test:remove:libc-musl = "pm-qa"
RDEPENDS:packagegroup-meta-oe-test:remove:arm = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:mipsarch = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:powerpc = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:riscv64 = "fwts"
RDEPENDS:packagegroup-meta-oe-test:remove:riscv32 = "fwts"

RDEPENDS:packagegroup-meta-oe-ptest-packages = "\
    zeromq-ptest \
    leveldb-ptest \
    psqlodbc-ptest \
    protobuf-ptest \
    rsyslog-ptest \
    oprofile-ptest \
    libteam-ptest \
    uthash-ptest \
    libee-ptest \
    poco-ptest \
    cmocka-ptest \
    minicoredumper-ptest \
    hiredis-ptest \
"
RDEPENDS:packagegroup-meta-oe-ptest-packages:append:x86 = " mcelog-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:append:x86-64 = " mcelog-ptest"

RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:riscv64 = "oprofile-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:riscv32 = "oprofile-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:arm = "numactl-ptest"
RDEPENDS:packagegroup-meta-oe-ptest-packages:remove:libc-musl = "minicoredumper-ptest"


RDEPENDS:packagegroup-meta-oe-fortran-packages = "\
    lapack \
    octave \
    suitesparse \
"
# library-only or headers-only packages
# They wont be built as part of images but might be interesting to include
# with dev-pkgs images
#
# opencl-headers sdbus-c++-libsystemd nlohmann-fifo sqlite-orm
# nlohmann-json exprtk liblightmodbus p8platform gnome-doc-utils-stub
# glm ttf-mplus xbitmaps ceres-solver cli11 fftw gnulib libeigen ade
# spdlog span-lite uthash websocketpp catch2 properties-cpp cpp-netlib

# rsyslog conflicts with syslog-ng so its not included here

EXCLUDE_FROM_WORLD = "1"
