SUMMARY = "cbindgen creates C/C++11 headers for Rust libraries which expose a public C API"
HOMEPAGE = "https://github.com/mozilla/cbindgen"
LICENSE = "MPL-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9741c346eef56131163e13b9db1241b3"

SRC_URI += "git://github.com/mozilla/cbindgen.git;protocol=https;branch=master"
SRCREV = "bd78bbe59b10eda6ef1255e4acda95c56c6d0279"
S = "${WORKDIR}/git"

inherit cargo pkgconfig

SRC_URI += " \
    crate://crates.io/anstream/0.6.15 \
    crate://crates.io/anstyle-parse/0.2.5 \
    crate://crates.io/anstyle-query/1.1.1 \
    crate://crates.io/anstyle-wincon/3.0.4 \
    crate://crates.io/anstyle/1.0.8 \
    crate://crates.io/autocfg/1.3.0 \
    crate://crates.io/bitflags/2.6.0 \
    crate://crates.io/cfg-if/1.0.0 \
    crate://crates.io/clap/4.5.15 \
    crate://crates.io/clap_builder/4.5.15 \
    crate://crates.io/clap_lex/0.7.2 \
    crate://crates.io/colorchoice/1.0.2 \
    crate://crates.io/dashmap/5.5.3 \
    crate://crates.io/diff/0.1.13 \
    crate://crates.io/equivalent/1.0.1 \
    crate://crates.io/errno/0.3.9 \
    crate://crates.io/fastrand/2.1.0 \
    crate://crates.io/hashbrown/0.14.5 \
    crate://crates.io/heck/0.4.1 \
    crate://crates.io/indexmap/2.3.0 \
    crate://crates.io/is_terminal_polyfill/1.70.1 \
    crate://crates.io/itoa/1.0.11 \
    crate://crates.io/lazy_static/1.5.0 \
    crate://crates.io/libc/0.2.155 \
    crate://crates.io/linux-raw-sys/0.4.14 \
    crate://crates.io/lock_api/0.4.12 \
    crate://crates.io/log/0.4.22 \
    crate://crates.io/memchr/2.7.4 \
    crate://crates.io/once_cell/1.19.0 \
    crate://crates.io/parking_lot/0.12.3 \
    crate://crates.io/parking_lot_core/0.9.10 \
    crate://crates.io/pretty_assertions/1.4.0 \
    crate://crates.io/proc-macro2/1.0.86 \
    crate://crates.io/quote/1.0.36 \
    crate://crates.io/redox_syscall/0.5.3 \
    crate://crates.io/rustix/0.38.34 \
    crate://crates.io/ryu/1.0.18 \
    crate://crates.io/scopeguard/1.2.0 \
    crate://crates.io/serde/1.0.205 \
    crate://crates.io/serde_derive/1.0.205 \
    crate://crates.io/serde_json/1.0.122 \
    crate://crates.io/serde_spanned/0.6.7 \
    crate://crates.io/serial_test/2.0.0 \
    crate://crates.io/serial_test_derive/2.0.0 \
    crate://crates.io/smallvec/1.13.2 \
    crate://crates.io/strsim/0.11.1 \
    crate://crates.io/syn/2.0.85 \
    crate://crates.io/tempfile/3.12.0 \
    crate://crates.io/toml/0.8.19 \
    crate://crates.io/toml_datetime/0.6.8 \
    crate://crates.io/toml_edit/0.22.20 \
    crate://crates.io/unicode-ident/1.0.12 \
    crate://crates.io/utf8parse/0.2.2 \
    crate://crates.io/windows-sys/0.52.0 \
    crate://crates.io/windows-sys/0.59.0 \
    crate://crates.io/windows-targets/0.52.6 \
    crate://crates.io/windows_aarch64_gnullvm/0.52.6 \
    crate://crates.io/windows_aarch64_msvc/0.52.6 \
    crate://crates.io/windows_i686_gnu/0.52.6 \
    crate://crates.io/windows_i686_gnullvm/0.52.6 \
    crate://crates.io/windows_i686_msvc/0.52.6 \
    crate://crates.io/windows_x86_64_gnu/0.52.6 \
    crate://crates.io/windows_x86_64_gnullvm/0.52.6 \
    crate://crates.io/windows_x86_64_msvc/0.52.6 \
    crate://crates.io/winnow/0.6.18 \
    crate://crates.io/yansi/0.5.1 \
"

SRC_URI[anstream-0.6.15.sha256sum] = "64e15c1ab1f89faffbf04a634d5e1962e9074f2741eef6d97f3c4e322426d526"
SRC_URI[anstyle-parse-0.2.5.sha256sum] = "eb47de1e80c2b463c735db5b217a0ddc39d612e7ac9e2e96a5aed1f57616c1cb"
SRC_URI[anstyle-query-1.1.1.sha256sum] = "6d36fc52c7f6c869915e99412912f22093507da8d9e942ceaf66fe4b7c14422a"
SRC_URI[anstyle-wincon-3.0.4.sha256sum] = "5bf74e1b6e971609db8ca7a9ce79fd5768ab6ae46441c572e46cf596f59e57f8"
SRC_URI[anstyle-1.0.8.sha256sum] = "1bec1de6f59aedf83baf9ff929c98f2ad654b97c9510f4e70cf6f661d49fd5b1"
SRC_URI[autocfg-1.3.0.sha256sum] = "0c4b4d0bd25bd0b74681c0ad21497610ce1b7c91b1022cd21c80c6fbdd9476b0"
SRC_URI[bitflags-2.6.0.sha256sum] = "b048fb63fd8b5923fc5aa7b340d8e156aec7ec02f0c78fa8a6ddc2613f6f71de"
SRC_URI[cfg-if-1.0.0.sha256sum] = "baf1de4339761588bc0619e3cbc0120ee582ebb74b53b4efbf79117bd2da40fd"
SRC_URI[clap-4.5.15.sha256sum] = "11d8838454fda655dafd3accb2b6e2bea645b9e4078abe84a22ceb947235c5cc"
SRC_URI[clap_builder-4.5.15.sha256sum] = "216aec2b177652e3846684cbfe25c9964d18ec45234f0f5da5157b207ed1aab6"
SRC_URI[clap_lex-0.7.2.sha256sum] = "1462739cb27611015575c0c11df5df7601141071f07518d56fcc1be504cbec97"
SRC_URI[colorchoice-1.0.2.sha256sum] = "d3fd119d74b830634cea2a0f58bbd0d54540518a14397557951e79340abc28c0"
SRC_URI[dashmap-5.5.3.sha256sum] = "978747c1d849a7d2ee5e8adc0159961c48fb7e5db2f06af6723b80123bb53856"
SRC_URI[diff-0.1.13.sha256sum] = "56254986775e3233ffa9c4d7d3faaf6d36a2c09d30b20687e9f88bc8bafc16c8"
SRC_URI[equivalent-1.0.1.sha256sum] = "5443807d6dff69373d433ab9ef5378ad8df50ca6298caf15de6e52e24aaf54d5"
SRC_URI[errno-0.3.9.sha256sum] = "534c5cf6194dfab3db3242765c03bbe257cf92f22b38f6bc0c58d59108a820ba"
SRC_URI[fastrand-2.1.0.sha256sum] = "9fc0510504f03c51ada170672ac806f1f105a88aa97a5281117e1ddc3368e51a"
SRC_URI[hashbrown-0.14.5.sha256sum] = "e5274423e17b7c9fc20b6e7e208532f9b19825d82dfd615708b70edd83df41f1"
SRC_URI[heck-0.4.1.sha256sum] = "95505c38b4572b2d910cecb0281560f54b440a19336cbbcb27bf6ce6adc6f5a8"
SRC_URI[indexmap-2.3.0.sha256sum] = "de3fc2e30ba82dd1b3911c8de1ffc143c74a914a14e99514d7637e3099df5ea0"
SRC_URI[is_terminal_polyfill-1.70.1.sha256sum] = "7943c866cc5cd64cbc25b2e01621d07fa8eb2a1a23160ee81ce38704e97b8ecf"
SRC_URI[itoa-1.0.11.sha256sum] = "49f1f14873335454500d59611f1cf4a4b0f786f9ac11f4312a78e4cf2566695b"
SRC_URI[lazy_static-1.5.0.sha256sum] = "bbd2bcb4c963f2ddae06a2efc7e9f3591312473c50c6685e1f298068316e66fe"
SRC_URI[libc-0.2.155.sha256sum] = "97b3888a4aecf77e811145cadf6eef5901f4782c53886191b2f693f24761847c"
SRC_URI[linux-raw-sys-0.4.14.sha256sum] = "78b3ae25bc7c8c38cec158d1f2757ee79e9b3740fbc7ccf0e59e4b08d793fa89"
SRC_URI[lock_api-0.4.12.sha256sum] = "07af8b9cdd281b7915f413fa73f29ebd5d55d0d3f0155584dade1ff18cea1b17"
SRC_URI[log-0.4.22.sha256sum] = "a7a70ba024b9dc04c27ea2f0c0548feb474ec5c54bba33a7f72f873a39d07b24"
SRC_URI[memchr-2.7.4.sha256sum] = "78ca9ab1a0babb1e7d5695e3530886289c18cf2f87ec19a575a0abdce112e3a3"
SRC_URI[once_cell-1.19.0.sha256sum] = "3fdb12b2476b595f9358c5161aa467c2438859caa136dec86c26fdd2efe17b92"
SRC_URI[parking_lot-0.12.3.sha256sum] = "f1bf18183cf54e8d6059647fc3063646a1801cf30896933ec2311622cc4b9a27"
SRC_URI[parking_lot_core-0.9.10.sha256sum] = "1e401f977ab385c9e4e3ab30627d6f26d00e2c73eef317493c4ec6d468726cf8"
SRC_URI[pretty_assertions-1.4.0.sha256sum] = "af7cee1a6c8a5b9208b3cb1061f10c0cb689087b3d8ce85fb9d2dd7a29b6ba66"
SRC_URI[proc-macro2-1.0.86.sha256sum] = "5e719e8df665df0d1c8fbfd238015744736151d4445ec0836b8e628aae103b77"
SRC_URI[quote-1.0.36.sha256sum] = "0fa76aaf39101c457836aec0ce2316dbdc3ab723cdda1c6bd4e6ad4208acaca7"
SRC_URI[redox_syscall-0.5.3.sha256sum] = "2a908a6e00f1fdd0dfd9c0eb08ce85126f6d8bbda50017e74bc4a4b7d4a926a4"
SRC_URI[rustix-0.38.34.sha256sum] = "70dc5ec042f7a43c4a73241207cecc9873a06d45debb38b329f8541d85c2730f"
SRC_URI[ryu-1.0.18.sha256sum] = "f3cb5ba0dc43242ce17de99c180e96db90b235b8a9fdc9543c96d2209116bd9f"
SRC_URI[scopeguard-1.2.0.sha256sum] = "94143f37725109f92c262ed2cf5e59bce7498c01bcc1502d7b9afe439a4e9f49"
SRC_URI[serde-1.0.205.sha256sum] = "e33aedb1a7135da52b7c21791455563facbbcc43d0f0f66165b42c21b3dfb150"
SRC_URI[serde_derive-1.0.205.sha256sum] = "692d6f5ac90220161d6774db30c662202721e64aed9058d2c394f451261420c1"
SRC_URI[serde_json-1.0.122.sha256sum] = "784b6203951c57ff748476b126ccb5e8e2959a5c19e5c617ab1956be3dbc68da"
SRC_URI[serde_spanned-0.6.7.sha256sum] = "eb5b1b31579f3811bf615c144393417496f152e12ac8b7663bf664f4a815306d"
SRC_URI[serial_test-2.0.0.sha256sum] = "0e56dd856803e253c8f298af3f4d7eb0ae5e23a737252cd90bb4f3b435033b2d"
SRC_URI[serial_test_derive-2.0.0.sha256sum] = "91d129178576168c589c9ec973feedf7d3126c01ac2bf08795109aa35b69fb8f"
SRC_URI[smallvec-1.13.2.sha256sum] = "3c5e1a9a646d36c3599cd173a41282daf47c44583ad367b8e6837255952e5c67"
SRC_URI[strsim-0.11.1.sha256sum] = "7da8b5736845d9f2fcb837ea5d9e2628564b3b043a70948a3f0b778838c5fb4f"
SRC_URI[syn-2.0.85.sha256sum] = "5023162dfcd14ef8f32034d8bcd4cc5ddc61ef7a247c024a33e24e1f24d21b56"
SRC_URI[tempfile-3.12.0.sha256sum] = "04cbcdd0c794ebb0d4cf35e88edd2f7d2c4c3e9a5a6dab322839b321c6a87a64"
SRC_URI[toml-0.8.19.sha256sum] = "a1ed1f98e3fdc28d6d910e6737ae6ab1a93bf1985935a1193e68f93eeb68d24e"
SRC_URI[toml_datetime-0.6.8.sha256sum] = "0dd7358ecb8fc2f8d014bf86f6f638ce72ba252a2c3a2572f2a795f1d23efb41"
SRC_URI[toml_edit-0.22.20.sha256sum] = "583c44c02ad26b0c3f3066fe629275e50627026c51ac2e595cca4c230ce1ce1d"
SRC_URI[unicode-ident-1.0.12.sha256sum] = "3354b9ac3fae1ff6755cb6db53683adb661634f67557942dea4facebec0fee4b"
SRC_URI[utf8parse-0.2.2.sha256sum] = "06abde3611657adf66d383f00b093d7faecc7fa57071cce2578660c9f1010821"
SRC_URI[windows-sys-0.52.0.sha256sum] = "282be5f36a8ce781fad8c8ae18fa3f9beff57ec1b52cb3de0789201425d9a33d"
SRC_URI[windows-sys-0.59.0.sha256sum] = "1e38bc4d79ed67fd075bcc251a1c39b32a1776bbe92e5bef1f0bf1f8c531853b"
SRC_URI[windows-targets-0.52.6.sha256sum] = "9b724f72796e036ab90c1021d4780d4d3d648aca59e491e6b98e725b84e99973"
SRC_URI[windows_aarch64_gnullvm-0.52.6.sha256sum] = "32a4622180e7a0ec044bb555404c800bc9fd9ec262ec147edd5989ccd0c02cd3"
SRC_URI[windows_aarch64_msvc-0.52.6.sha256sum] = "09ec2a7bb152e2252b53fa7803150007879548bc709c039df7627cabbd05d469"
SRC_URI[windows_i686_gnu-0.52.6.sha256sum] = "8e9b5ad5ab802e97eb8e295ac6720e509ee4c243f69d781394014ebfe8bbfa0b"
SRC_URI[windows_i686_gnullvm-0.52.6.sha256sum] = "0eee52d38c090b3caa76c563b86c3a4bd71ef1a819287c19d586d7334ae8ed66"
SRC_URI[windows_i686_msvc-0.52.6.sha256sum] = "240948bc05c5e7c6dabba28bf89d89ffce3e303022809e73deaefe4f6ec56c66"
SRC_URI[windows_x86_64_gnu-0.52.6.sha256sum] = "147a5c80aabfbf0c7d901cb5895d1de30ef2907eb21fbbab29ca94c5b08b1a78"
SRC_URI[windows_x86_64_gnullvm-0.52.6.sha256sum] = "24d5b23dc417412679681396f2b49f3de8c1473deb516bd34410872eff51ed0d"
SRC_URI[windows_x86_64_msvc-0.52.6.sha256sum] = "589f6da84c646204747d1270a2a5661ea66ed1cced2631d546fdfb155959f9ec"
SRC_URI[winnow-0.6.18.sha256sum] = "68a9bda4691f099d435ad181000724da8e5899daa10713c2d432552b9ccd3a6f"
SRC_URI[yansi-0.5.1.sha256sum] = "09041cd90cf85f7f8b2df60c646f853b7f535ce68f85244eb6731cf89fa498ec"

BBCLASSEXTEND += "native"
