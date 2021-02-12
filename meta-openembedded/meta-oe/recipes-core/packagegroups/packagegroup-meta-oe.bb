SUMMARY = "Meta-oe ptest packagegroups"

PACKAGE_ARCH = "${MACHINE_ARCH}"
inherit packagegroup

PROVIDES = "${PACKAGES}"
PACKAGES = "\
    packagegroup-meta-oe \
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-connectivity \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-dbs \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-printing \
    packagegroup-meta-oe-shells \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-support \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
"
#PACKAGES += "packagegroup-meta-oe-fortran-packages"

RDEPENDS_packagegroup-meta-oe = "\
    packagegroup-meta-oe-benchmarks \
    packagegroup-meta-oe-bsp \
    packagegroup-meta-oe-connectivity \
    packagegroup-meta-oe-core \
    packagegroup-meta-oe-crypto \
    packagegroup-meta-oe-dbs \
    packagegroup-meta-oe-devtools \
    packagegroup-meta-oe-extended \
    packagegroup-meta-oe-kernel \
    packagegroup-meta-oe-multimedia \
    packagegroup-meta-oe-navigation \
    packagegroup-meta-oe-printing \
    packagegroup-meta-oe-security \
    packagegroup-meta-oe-shells \
    packagegroup-meta-oe-support \
    packagegroup-meta-oe-test \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-gnome", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "packagegroup-meta-oe-graphics", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "ptest", "packagegroup-meta-oe-ptest-packages", "", d)} \
"

RDEPENDS_packagegroup-meta-oe-benchmarks = "\
    bonnie++ \
    dbench \
    dhrystone \
    fio \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "glmark2", "", d)} \
    iozone3 \
    iperf2 \
    iperf3 \
    libc-bench \
    libhugetlbfs \
    linpack \
    lmbench \
    memtester \
    nbench-byte \
    phoronix-test-suite \
    s-suite \
    stressapptest \
    sysbench \
    tinymembench \
    tiobench \
    whetstone \
"
RDEPENDS_packagegroup-meta-oe-benchmarks_append_armv7a = " cpuburn-arm"
RDEPENDS_packagegroup-meta-oe-benchmarks_append_armv7ve = " cpuburn-arm"
RDEPENDS_packagegroup-meta-oe-benchmarks_append_aarch64 = " cpuburn-arm"

RDEPENDS_packagegroup-meta-oe-benchmarks_remove_mipsarch = "libhugetlbfs"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_mips64 = "tinymembench"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_mips64el = "tinymembench"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_riscv64 = "libhugetlbfs"
RDEPENDS_packagegroup-meta-oe-benchmarks_remove_riscv32 = "libhugetlbfs"

RDEPENDS_packagegroup-meta-oe-bsp ="\
    acpitool \
    cpufrequtils \
    edac-utils \
    firmwared \
    flashrom \
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
RDEPENDS_packagegroup-meta-oe-bsp_append_x86 = " ledmon"
RDEPENDS_packagegroup-meta-oe-bsp_append_x86-64 = " ledmon"

RDEPENDS_packagegroup-meta-oe-bsp_remove_libc-musl = "ledmon"
RDEPENDS_packagegroup-meta-oe-bsp_remove_mipsarch = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_powerpc = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_powerpc64 = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_powerpc64le = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_riscv64 = "efivar efibootmgr"
RDEPENDS_packagegroup-meta-oe-bsp_remove_riscv32 = "efivar efibootmgr"

RDEPENDS_packagegroup-meta-oe-connectivity ="\
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
    phonet-utils \
    rabbitmq-c \
    rfkill \
    rtorrent \
    ser2net \
    smstools3 \
    telepathy-glib \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "telepathy-idle", "", d)} \
    thrift \
    usbmuxd \
    wifi-test-suite \
    zabbix \
    czmq \
    zeromq \
"

RDEPENDS_packagegroup-meta-oe-connectivity_append_libc-glibc = " wvstreams wvdial"

# dracut needs dracut
RDEPENDS_packagegroup-meta-oe-core = "\
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
    opencl-icd-loader \
    proxy-libintl \
    safec \
    sdbus-c++-tools \
    sdbus-c++ \
    toybox \
    usleep \
    dbus-cxx \
"
RDEPENDS_packagegroup-meta-oe-core_append_libc-glibc = " glfw"
RDEPENDS_packagegroup-meta-oe-core_remove_riscv64 = "safec"
RDEPENDS_packagegroup-meta-oe-core_remove_riscv32 = "safec"

RDEPENDS_packagegroup-meta-oe-crypto ="\
    botan \
    cryptsetup \
    fsverity-utils \
    libkcapi \
    libmcrypt \
    libsodium \
    pkcs11-helper \
"
RDEPENDS_packagegroup-meta-oe-crypto_remove_riscv32 = "botan"

RDEPENDS_packagegroup-meta-oe-dbs ="\
    influxdb \
    leveldb \
    libdbi \
    mariadb \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "mysql-python", "", d)} \
    postgresql \
    psqlodbc \
    rocksdb \
    soci \
    sqlite \
"

RDEPENDS_packagegroup-meta-oe-devtools ="\
    abseil-cpp \
    apitrace \
    breakpad \
    bootchart \
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
    lemon \
    flatbuffers \
    heaptrack \
    libubox \
    ltrace \
    lua \
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
    ldns \
    libgee \
    libsombok3 \
    lshw \
    luaposix \
    capnproto-compiler \
    mpich \
    msgpack-c \
    mercurial \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "nodejs", "", d)} \
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
    rapidjson \
    sip3 \
    squashfs-tools-ng \
    uftrace \
    libxerces-c \
    xerces-c-samples \
    xmlrpc-c \
    yasm \
    json-schema-validator \
"
RDEPENDS_packagegroup-meta-oe-devtools_append_x86 = " cpuid msr-tools pmtools"
RDEPENDS_packagegroup-meta-oe-devtools_append_x86-64 = " cpuid msr-tools pcimem pmtools"
RDEPENDS_packagegroup-meta-oe-devtools_append_arm = " pcimem"
RDEPENDS_packagegroup-meta-oe-devtools_append_aarch64 = " pcimem"
RDEPENDS_packagegroup-meta-oe-devtools_append_libc-musl = " musl-nscd"

RDEPENDS_packagegroup-meta-oe-devtools_remove_arm = "concurrencykit"
RDEPENDS_packagegroup-meta-oe-devtools_remove_armv5 = "uftrace nodejs"
RDEPENDS_packagegroup-meta-oe-devtools_remove_mipsarch = "concurrencykit lshw ply uftrace"
RDEPENDS_packagegroup-meta-oe-devtools_remove_mips64 = "luajit nodejs"
RDEPENDS_packagegroup-meta-oe-devtools_remove_mips64el = "luajit nodejs"
RDEPENDS_packagegroup-meta-oe-devtools_remove_powerpc = "android-tools breakpad lshw luajit uftrace"
RDEPENDS_packagegroup-meta-oe-devtools_remove_powerpc64 = "android-tools lshw luajit uftrace"
RDEPENDS_packagegroup-meta-oe-devtools_remove_powerpc64le = "android-tools lshw luajit uftrace"
RDEPENDS_packagegroup-meta-oe-devtools_remove_riscv64 = "breakpad concurrencykit heaptrack lshw ltrace luajit nodejs ply uftrace"
RDEPENDS_packagegroup-meta-oe-devtools_remove_riscv32 = "breakpad concurrencykit heaptrack lshw ltrace luajit nodejs ply uftrace"
RDEPENDS_packagegroup-meta-oe-devtools_remove_aarch64 = "${@bb.utils.contains("TUNE_FEATURES", "crypto", "", "abseil-cpp", d)} concurrencykit"
RDEPENDS_packagegroup-meta-oe-devtools_remove_x86-64 = "${@bb.utils.contains("TUNE_FEATURES", "corei7", "", "abseil-cpp", d)}"
RDEPENDS_packagegroup-meta-oe-devtools_remove_x86 = "ply"

RDEPENDS_packagegroup-meta-oe-extended ="\
     bitwise \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 wayland opengl", "boinc-client", "", d)} \
     brotli \
     byacc \
     cmpi-bindings \
     collectd \
     cfengine-masterfiles \
     cfengine \
     ddrescue \
     dialog \
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
     libuio \
     ${@bb.utils.contains("DISTRO_FEATURES", "x11", "libwmf", "", d)} \
     lprng \
     icewm \
     md5deep \
     indent \
     jansson \
     nana \
     nicstat \
     ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "openlmi-tools", "", d)} \
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
     libnss-nisplus \
     libpwquality \
     ${@bb.utils.contains("DISTRO_FEATURES", "systemd", "libreport", "", d)} \
     libserialport \
     libstatgrab \
     lockfile-progs \
     logwatch \
     mailx \
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
     sigrok-cli \
     snappy \
     tipcutils \
     tiptop \
     tmux \
     triggerhappy \
     upm \
     vlock \
     volume-key \
     wxwidgets \
     zlog \
     zstd \
     redis-plus-plus \
"
RDEPENDS_packagegroup-meta-oe-extended_append_libc-musl = " libexecinfo"
RDEPENDS_packagegroup-meta-oe-extended_append_x86-64 = " pmdk libx86-1"
RDEPENDS_packagegroup-meta-oe-extended_append_x86 = " libx86-1"

RDEPENDS_packagegroup-meta-oe-extended_remove_libc-musl = "libnss-nisplus sysdig"
RDEPENDS_packagegroup-meta-oe-extended_remove_mipsarch = "upm mraa minifi-cpp tiptop"
RDEPENDS_packagegroup-meta-oe-extended_remove_mips = "sysdig"
RDEPENDS_packagegroup-meta-oe-extended_remove_powerpc = "upm mraa minifi-cpp"
RDEPENDS_packagegroup-meta-oe-extended_remove_powerpc64 = "upm mraa minifi-cpp"
RDEPENDS_packagegroup-meta-oe-extended_remove_powerpc64le = "upm mraa"
RDEPENDS_packagegroup-meta-oe-extended_remove_riscv64 = "upm mraa sysdig tiptop"
RDEPENDS_packagegroup-meta-oe-extended_remove_riscv32 = "upm mraa sysdig tiptop"

RDEPENDS_packagegroup-meta-oe-gnome ="\
    atkmm \
    gcab \
    gnome-common \
    gmime \
    libjcat \
    gtk+ \
    gtkmm3 \
    gtkmm \
    libpeas \
    pyxdg \
    vte9 \
    gnome-theme-adwaita \
    libxmlb \
"

RDEPENDS_packagegroup-meta-oe-graphics ="\
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
    obconf \
    openbox \
    packagegroup-fonts-truetype \
    qrencode \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "st", "", d)} \
    takao-fonts \
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
    xsetmode \
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
    liblbxutil \
    libxaw6 \
    libxkbui \
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
    tigervnc \
    tslib \
    unclutter-xfixes \
    libvdpau \
    xcursorgen \
    xscreensaver \
    yad \
    parallel-deqp-runner \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl", "opengl-es-cts", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "opengl vulkan", "vulkan-cts", "", d)} \
"
RDEPENDS_packagegroup-meta-oe-graphics_append_x86 = " renderdoc xf86-video-nouveau xf86-video-mga"
RDEPENDS_packagegroup-meta-oe-graphics_append_x86-64 = " renderdoc xf86-video-nouveau xf86-video-mga"
RDEPENDS_packagegroup-meta-oe-graphics_append_arm = " renderdoc"
RDEPENDS_packagegroup-meta-oe-graphics_append_aarch64 = " renderdoc"

RDEPENDS_packagegroup-meta-oe-graphics_remove_libc-musl = "renderdoc"

RDEPENDS_packagegroup-meta-oe-kernel ="\
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
"
RDEPENDS_packagegroup-meta-oe-kernel_append_x86 = " intel-speed-select ipmiutil pm-graph turbostat"
RDEPENDS_packagegroup-meta-oe-kernel_append_x86-64 = " intel-speed-select ipmiutil kpatch pm-graph turbostat"
RDEPENDS_packagegroup-meta-oe-kernel_append_poerpc64 = " libpfm4"

# Kernel-selftest does not build with 5.8 and its exluded from build too so until its fixed remove it
RDEPENDS_packagegroup-meta-oe-kernel_remove = "kernel-selftest"
RDEPENDS_packagegroup-meta-oe-kernel_remove_libc-musl = "crash intel-speed-select kernel-selftest minicoredumper turbostat"

RDEPENDS_packagegroup-meta-oe-kernel_remove_mipsarch = "makedumpfile"
RDEPENDS_packagegroup-meta-oe-kernel_remove_mips64 = "crash"
RDEPENDS_packagegroup-meta-oe-kernel_remove_mips64el = "crash"

RDEPENDS_packagegroup-meta-oe-kernel_remove_riscv64 = "crash makedumpfile oprofile"
RDEPENDS_packagegroup-meta-oe-kernel_remove_riscv32 = "crash makedumpfile oprofile"

RDEPENDS_packagegroup-meta-oe-multimedia ="\
    alsa-oss \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "faad2", "", d)} \
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
    libburn \
    libcdio-paranoia \
    libcdio \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "libmad", "", d)} \
    libmms \
    libdvdread \
    libopus \
    live555-examples \
    live555-mediaserver \
    libmikmod \
    opus-tools \
    libmodplug \
    sound-theme-freedesktop \
    yavta \
    v4l-utils \
    wavpack \
    libvpx \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "xsp", "", d)} \
    ${@bb.utils.contains("LICENSE_FLAGS_WHITELIST", "commercial", "mpv", "", d)} \
    pipewire \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "pavucontrol", "", d)} \
    libopusenc \
"

RDEPENDS_packagegroup-meta-oe-multimedia_remove_libc-musl = "alsa-oss"

RDEPENDS_packagegroup-meta-oe-navigation ="\
    geos \
    ${@bb.utils.contains("DISTRO_FEATURES", "bluz4", "gpsd-machine-conf gpsd", "", d)} \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "orrery", "", d)} \
    geoclue \
    libspatialite \
    proj \
"

RDEPENDS_packagegroup-meta-oe-printing ="\
    cups-filters \
    qpdf \
"

RDEPENDS_packagegroup-meta-oe-security ="\
    keyutils \
    nmap \
    ${@bb.utils.contains("DISTRO_FEATURES", "pam", "passwdqc", "", d)} \
    softhsm \
    tomoyo-tools \
"

RDEPENDS_packagegroup-meta-oe-shells ="\
    dash \
    mksh \
    tcsh \
    zsh \
"

RDEPENDS_packagegroup-meta-oe-support ="\
    anthy \
    atop \
    ace-cloud-editor \
    frame \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "geis", "", d)} \
    geis \
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
    dstat \
    espeak \
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
    htop \
    hunspell-dictionaries \
    hunspell \
    hwdata \
    iksemel \
    gengetopt \
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
    ccid \
    zchunk \
    libgpiod \
    libgpiod \
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
    libjs-jquery \
    libjs-sizzle \
    liblinebreak \
    mailcap \
    liboauth \
    libol \
    mg \
    monit \
    mscgen \
    libsmi \
    remmina \
    neon \
    nmon \
    libtinyxml \
    libusbg \
    libutempter \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "links-x11", "links", d)} \
    ${@bb.utils.contains("BBFILE_COLLECTIONS", "meta-python2", "lio-utils", "", d)} \
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
    sdparm \
    serial-forward \
    read-edid \
    spitools \
    libsass \
    sassc \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "synergy", "", d)} \
    syslog-ng \
    system-config-keyboard \
    tbb \
    satyr \
    pcsc-lite \
    pcsc-tools \
    sharutils \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11", "toscoterm", "", d)} \
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
    xxhash \
    unicode-ucd \
    xdelta3 \
    uriparser \
    nano \
    xdg-user-dirs \
    xmlsec1 \
    ${@bb.utils.contains("DISTRO_FEATURES", "x11 pam", "xorgxrdp xrdp", "", d)} \
    usb-modeswitch-data \
    usb-modeswitch \
    liburing \
    zbar \
    libmicrohttpd \
    yaml-cpp \
"
RDEPENDS_packagegroup-meta-oe-support_append_armv7a = " ne10"
RDEPENDS_packagegroup-meta-oe-support_append_armv7ve = " ne10"
RDEPENDS_packagegroup-meta-oe-support_append_aarch64 = " ne10"
RDEPENDS_packagegroup-meta-oe-support_append_x86 = " mcelog mce-inject mce-test open-vm-tools vboxguestdrivers"
RDEPENDS_packagegroup-meta-oe-support_append_x86-64 = " mcelog mce-inject mce-test open-vm-tools vboxguestdrivers"
RDEPENDS_packagegroup-meta-oe-support_remove_arm ="numactl"
RDEPENDS_packagegroup-meta-oe-support_remove_mipsarch = "gperftools"
RDEPENDS_packagegroup-meta-oe-support_remove_riscv64 = "gperftools uim"
RDEPENDS_packagegroup-meta-oe-support_remove_riscv32 = "gperftools uim"
RDEPENDS_packagegroup-meta-oe-support_remove_powerpc = "ssiapi"

RDEPENDS_packagegroup-meta-oe-test ="\
    bats \
    cmocka \
    cppunit \
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
RDEPENDS_packagegroup-meta-oe-test_remove_libc-musl = "pm-qa"
RDEPENDS_packagegroup-meta-oe-test_remove_arm = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_mipsarch = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_powerpc = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_riscv64 = "fwts"
RDEPENDS_packagegroup-meta-oe-test_remove_riscv32 = "fwts"

RDEPENDS_packagegroup-meta-oe-ptest-packages = "\
    zeromq-ptest \
    leveldb-ptest \
    psqlodbc-ptest \
    lua-ptest \
    protobuf-ptest \
    rsyslog-ptest \
    oprofile-ptest \
    libteam-ptest \
    uthash-ptest \
    libee-ptest \
    poco-ptest \
    cmocka-ptest \
"
RDEPENDS_packagegroup-meta-oe-ptest-packages_append_x86 = " mcelog-ptest"
RDEPENDS_packagegroup-meta-oe-ptest-packages_append_x86-64 = " mcelog-ptest"

RDEPENDS_packagegroup-meta-oe-ptest-packages_remove_riscv64 = "oprofile-ptest"
RDEPENDS_packagegroup-meta-oe-ptest-packages_remove_riscv32 = "oprofile-ptest"
RDEPENDS_packagegroup-meta-oe-ptest-packages_remove_arm = "numactl-ptest"


RDEPENDS_packagegroup-meta-oe-fortran-packages = "\
    lapack \
    octave \
    suitesparse \
"
# library-only or headers-only packages
# They wont be built as part of images but might be interesting to include
# with dev-pkgs images
#
# opencl-headers sdbus-c++-libsystemd boost-url nlohmann-fifo sqlite-orm
# nlohmann-json exprtk liblightmodbus p8platform gnome-doc-utils-stub
# glm ttf-mplus xbitmaps ceres-solver cli11 fftw gnulib libeigen ade
# spdlog span-lite uthash websocketpp catch2 properties-cpp cpp-netlib
# cereal
# rsyslog conflicts with syslog-ng so its not included here

EXCLUDE_FROM_WORLD = "1"
