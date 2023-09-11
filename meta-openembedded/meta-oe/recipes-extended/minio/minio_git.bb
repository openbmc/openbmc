HOMEPAGE = "https://github.com/minio/mc"
SUMMARY = "MinIO Client is a replacement for ls, cp, mkdir, diff and rsync commands for filesystems and object storage."
DESCRIPTION = "MinIO Client (mc) provides a modern alternative to \
               UNIX commands like ls, cat, cp, mirror, diff, find \
               etc. It supports filesystems and Amazon S3 compatible \
               cloud storage service (AWS Signature v2 and v4). \
"

SRC_URI = "git://github.com/minio/mc;branch=master;name=mc;protocol=https \
           file://modules.txt \
          "

include src_uri.inc

SRCREV_mc = "01b87ecc02ffad47dfe13c2154ac31db3e3115df"

SRCREV_FORMAT .= "_mc"

GO_IMPORT = "import"

LICENSE = "AGPL-3.0-only"
LIC_FILES_CHKSUM = "file://src/${GO_IMPORT}/LICENSE;md5=eb1e647870add0502f8f010b19de32af"

PV = "${SRCREV_mc}"

inherit go
inherit goarch

# | ./github.com/minio/mc/main.go:27:(.text+0xd258b8): relocation R_MIPS_HI16 against `a local symbol' cannot be used when making a shared object; recompile with -fPIC
COMPATIBLE_HOST:mips = "null"
# ERROR: QA Issue: minio: ELF binary /usr/sbin/mc has relocations in .text [textrel]
# Needs fixing with go >= 1.20.4"
EXCLUDE_FROM_WORLD = "1"

DEPENDS += "rsync-native"

do_compile() {
 
    cd ${S}/src/${GO_IMPORT}

    export GOFLAGS="-mod=vendor"
    export GOPATH="$GOPATH:${S}/src/import/.gopath:${S}/src/import/vendor"
    sites="github.com/charmbracelet/bubbletea:github.com/charmbracelet/bubbletea \
           github.com/cheggaaa/pb:github.com/cheggaaa/pb \
           github.com/dustin/go-humanize:github.com/dustin/go-humanize \
           github.com/fatih/color:github.com/fatih/color \
           github.com/go-ole/go-ole:github.com/go-ole/go-ole \
           github.com/goccy/go-json:github.com/goccy/go-json \
           github.com/google/shlex:github.com/google/shlex \
           github.com/google/uuid:github.com/google/uuid \
           github.com/inconshreveable/mousetrap:github.com/inconshreveable/mousetrap \
           github.com/json-iterator/go:github.com/json-iterator/go \
           github.com/klauspost/compress:github.com/klauspost/compress \
           github.com/mattn/go-ieproxy:github.com/mattn/go-ieproxy \
           github.com/mattn/go-isatty:github.com/mattn/go-isatty \
           github.com/minio/cli:github.com/minio/cli \
           github.com/minio/colorjson:github.com/minio/colorjson \
           github.com/minio/filepath:github.com/minio/filepath \
           github.com/minio/madmin-go:github.com/minio/madmin-go \
           github.com/minio/md5-simd:github.com/minio/md5-simd \
           github.com/minio/minio-go/v7:github.com/minio/minio-go/v7 \
           github.com/minio/pkg:github.com/minio/pkg \
           github.com/minio/selfupdate:github.com/minio/selfupdate \
           github.com/minio/sha256-simd:github.com/minio/sha256-simd \
           github.com/mitchellh/go-homedir:github.com/mitchellh/go-homedir \
           github.com/pkg/xattr:github.com/pkg/xattr \
           github.com/posener/complete:github.com/posener/complete \
           github.com/prometheus/client_golang:github.com/prometheus/client_golang \
           github.com/prometheus/prom2json:github.com/prometheus/prom2json \
           github.com/rjeczalik/notify:github.com/rjeczalik/notify \
           github.com/rs/xid:github.com/rs/xid \
           github.com/secure-io/sio-go:github.com/secure-io/sio-go \
           github.com/shirou/gopsutil/v3:github.com/shirou/gopsutil/v3 \
           github.com/tidwall/gjson:github.com/tidwall/gjson \
           golang.org/x/crypto:go.googlesource.com/crypto \
           golang.org/x/net:go.googlesource.com/net \
           golang.org/x/text:go.googlesource.com/text \
           gopkg.in/check.v1:gopkg.in/check.v1 \
           gopkg.in/h2non/filetype.v1:gopkg.in/h2non/filetype.v1 \
           gopkg.in/yaml.v2:gopkg.in/yaml.v2 \
           github.com/charmbracelet/bubbles:github.com/charmbracelet/bubbles \
           github.com/charmbracelet/lipgloss:github.com/charmbracelet/lipgloss \
           github.com/gdamore/tcell/v2:github.com/gdamore/tcell/v2 \
           github.com/golang-jwt/jwt/v4:github.com/golang-jwt/jwt/v4 \
           github.com/navidys/tvxwidgets:github.com/navidys/tvxwidgets \
           github.com/olekukonko/tablewriter:github.com/olekukonko/tablewriter \
           github.com/prometheus/client_model:github.com/prometheus/client_model \
           github.com/rivo/tview:github.com/rivo/tview \
           github.com/tinylib/msgp:github.com/tinylib/msgp \
           golang.org/x/term:go.googlesource.com/term \
           github.com/beorn7/perks:github.com/beorn7/perks \
           github.com/cespare/xxhash/v2:github.com/cespare/xxhash/v2 \
           github.com/containerd/console:github.com/containerd/console \
           github.com/coreos/go-semver:github.com/coreos/go-semver \
           github.com/coreos/go-systemd/v22:github.com/coreos/go-systemd/v22 \
           github.com/decred/dcrd/dcrec/secp256k1/v4:github.com/decred/dcrd/dcrec/secp256k1/v4//dcrec/secp256k1 \
           github.com/fatih/structs:github.com/fatih/structs \
           github.com/gdamore/encoding:github.com/gdamore/encoding \
           github.com/gogo/protobuf:github.com/gogo/protobuf \
           github.com/golang/protobuf:github.com/golang/protobuf \
           github.com/hashicorp/errwrap:github.com/hashicorp/errwrap \
           github.com/hashicorp/go-multierror:github.com/hashicorp/go-multierror \
           github.com/klauspost/cpuid/v2:github.com/klauspost/cpuid/v2 \
           github.com/kr/text:github.com/kr/text \
           github.com/lestrrat-go/backoff/v2:github.com/lestrrat-go/backoff/v2 \
           github.com/lestrrat-go/blackmagic:github.com/lestrrat-go/blackmagic \
           github.com/lestrrat-go/httpcc:github.com/lestrrat-go/httpcc \
           github.com/lestrrat-go/iter:github.com/lestrrat-go/iter \
           github.com/lestrrat-go/jwx:github.com/lestrrat-go/jwx \
           github.com/lestrrat-go/option:github.com/lestrrat-go/option \
           github.com/lucasb-eyer/go-colorful:github.com/lucasb-eyer/go-colorful \
           github.com/lufia/plan9stats:github.com/lufia/plan9stats \
           github.com/mattn/go-colorable:github.com/mattn/go-colorable \
           github.com/mattn/go-runewidth:github.com/mattn/go-runewidth \
           github.com/matttproud/golang_protobuf_extensions:github.com/matttproud/golang_protobuf_extensions \
           github.com/modern-go/concurrent:github.com/modern-go/concurrent \
           github.com/modern-go/reflect2:github.com/modern-go/reflect2 \
           github.com/muesli/ansi:github.com/muesli/ansi \
           github.com/muesli/reflow:github.com/muesli/reflow \
           github.com/muesli/termenv:github.com/muesli/termenv \
           github.com/niemeyer/pretty:github.com/niemeyer/pretty \
           github.com/philhofer/fwd:github.com/philhofer/fwd \
           github.com/pkg/errors:github.com/pkg/errors \
           github.com/power-devops/perfstat:github.com/power-devops/perfstat \
           github.com/prometheus/common:github.com/prometheus/common \
           github.com/prometheus/procfs:github.com/prometheus/procfs \
           github.com/rivo/uniseg:github.com/rivo/uniseg \
           github.com/sirupsen/logrus:github.com/sirupsen/logrus \
           github.com/tidwall/match:github.com/tidwall/match \
           github.com/tidwall/pretty:github.com/tidwall/pretty \
           github.com/tklauser/go-sysconf:github.com/tklauser/go-sysconf \
           github.com/tklauser/numcpus:github.com/tklauser/numcpus \
           github.com/yusufpapurcu/wmi:github.com/yusufpapurcu/wmi \
           go.etcd.io/etcd/api/v3:github.com/etcd-io/etcd/api/v3//api \
           go.etcd.io/etcd/client/pkg/v3:github.com/etcd-io/etcd/api/v3//client/pkg \
           go.etcd.io/etcd/client/v3:github.com/etcd-io/etcd/api/v3//client/v3 \
           go.uber.org/atomic:go.uber.org/atomic \
           go.uber.org/multierr:go.uber.org/multierr \
           go.uber.org/zap:go.uber.org/zap \
           golang.org/x/sync:go.googlesource.com/sync \
           golang.org/x/sys:go.googlesource.com/sys \
           google.golang.org/genproto:google.golang.org/genproto \
           google.golang.org/grpc:google.golang.org/grpc \
           google.golang.org/protobuf:google.golang.org/protobuf \
           gopkg.in/ini.v1:gopkg.in/ini.v1 \
           gopkg.in/yaml.v3:gopkg.in/yaml.v3"
    for s in $sites; do
        site_dest=$(echo $s | cut -d: -f1)
        site_source=$(echo $s | cut -d: -f2)
        mkdir -p vendor.copy/$site_dest
        [ -n "$(ls -A vendor.copy/$site_dest/*.go 2> /dev/null)" ] && { echo "[INFO] vendor.fetch/$site_source -> $site_dest: go copy skipped (files present)" ; true ; } || { echo "[INFO] $site_dest: copying .go files" ; rsync -a --exclude='vendor/' --exclude='.git/' vendor.fetch/$site_source/ vendor.copy/$site_dest ; }
    done

    ln -sf vendor.copy vendor
    # these are bad symlinks, go validates them and breaks the build if they are present
    rm -f vendor/go.etcd.io/etcd/client/v3/example_*

    cp ${WORKDIR}/modules.txt vendor/

    ${GO} build -trimpath
}

do_install() {
    install -d ${D}/${sbindir}
    install ${S}/src/${GO_IMPORT}/mc ${D}/${sbindir}/mc
}
