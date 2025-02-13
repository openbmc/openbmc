# patchtest selftests for openembedded-core

This directory provides a test suite and selftest script for use with the
patchtest repository: <https://git.yoctoproject.org/patchtest/>

To setup for use:

1. Clone <https://git.openembedded.org/openembedded-core> (this repo) and <https://git.openembedded.org/bitbake/>
2. Clone <https://git.yoctoproject.org/patchtest>
3. Install the necessary Python modules: in meta/lib/patchtest or the patchtest
   repo, do `pip install -r requirements.txt`
4. Add patchtest to PATH: `export PATH=/path/to/patchtest/repo:$PATH`
5. Initialize the environment: `source oe-init-build-env`
6. Add meta-selftest to bblayers.conf: `bitbake-layers add-layer
   /path/to/meta-selftest/` (the selftests use this layer's recipes as test
   targets)
7. Finally, run the selftest script: `./meta/lib/patchtest/selftest/selftest`

For more information on using patchtest, see the patchtest repo at
<https://git.yoctoproject.org/patchtest/>.
