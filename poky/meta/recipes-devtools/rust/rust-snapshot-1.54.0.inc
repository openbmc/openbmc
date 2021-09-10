require rust-snapshot.inc

## This is information on the rust-snapshot (binary) used to build our current release.
## snapshot info is taken from rust/src/stage0.txt
## TODO: find a way to add additional SRC_URIs based on the contents of an
##       earlier SRC_URI.
RS_VERSION = "1.53.0"
CARGO_VERSION = "1.53.0"

# TODO: Add hashes for other architecture toolchains as well. Make a script?
SRC_URI[rust-std-snapshot-x86_64.sha256sum] = "66d5257bbd194db08e67ca63a74cc80cdc4a36eaa30bf7dfe65861d9deaff7f7"
SRC_URI[rustc-snapshot-x86_64.sha256sum] = "9c2f0443a546dd18f1f020c7711a7b9864432545ceb812e79315c1e76508b193"
SRC_URI[cargo-snapshot-x86_64.sha256sum] = "e4437f6c83574fc60e183f4df439190f7610e4a669476802795645da66fbb83b"

SRC_URI[rust-std-snapshot-aarch64.sha256sum] = "fc261d0ff057c58b36057c30217bad1a29a9a3f257c70f5df88ad3f1d982386e"
SRC_URI[rustc-snapshot-aarch64.sha256sum] = "a857e5c615f6b5b3afc6b707ae007b3b4624585b0b2ba9b38b553114db37f8d5"
SRC_URI[cargo-snapshot-aarch64.sha256sum] = "4fa425a2d4633aeca67fa42695b3b9100f8b988dd1c21e60d6b2aa1164e55c0c"

