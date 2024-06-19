# **CI for Yocto Project and meta-arm**
# **CI for Yocto Project**
The Yocto Project has an autobuilder that performs nightly builds and image tests on all of the defined QEMU machines, including qemuarm and qemuarm64  Also, it currently runs builds on the hardware reference platforms including genericarm64 and meta-arm mahines fvp-base and sbsa-ref.  More information on the autobuilder can be found at <https://autobuilder.yoctoproject.org/>.

More information on the image tests can be found at <https://wiki.yoctoproject.org/wiki/Image_tests>.

The Yocto Project also has the ability to have individual package tests, ptests.  For more information on those, go to <https://wiki.yoctoproject.org/wiki/Ptest>.
# **CI for meta-arm**
meta-arm is using the Gitlab CI infrastructure.  This is currently being done internal to Arm, but an external version can be seen at <https://gitlab.com/jonmason00/meta-arm/-/pipelines>.

This CI is constantly being expanded to provide increased coverage of the software and hardware supported in meta-arm.  All platforms are required to add a kas file and `.gitlab-ci.yml` entry as part of the initial patch series.  More information on kas can be found at <https://github.com/siemens/kas>.

To this end, it would be wise to run kas locally to verify everything works prior to pushing to the CI build system.
## **Running kas locally**
### **Install kas**
kas can be installed with pip, for example:
```
$ pip3 install --user kas
```

See <https://kas.readthedocs.io/en/latest/userguide/getting-started.html> for information on the dependencies and more.

This assumes that the kas path ($HOME/.local/bin) is in $PATH.  If not, the user will need to manually add this or the kas command will not be found.

### **Run kas locally**
```
$ cd ~/meta-arm/
$ kas build kas/juno.yml
```

By default kas will create a build directory under meta-arm to contain the checked out layers, build directory, and downloads.  You can change this by setting environment variables.  DL\_DIR and SSTATE\_DIR are respected so these can point at existing directories, and setting KAS\_WORK\_DIR to the directory where repositories are already cloned will save having to re-fetch.  This can look something like:
```
$ SSTATE_DIR=/builds/persist/sstate DL_DIR=/builds/persist/downloads kas build ci/qemuarm64.yml:ci/testimage.yml
```

See the [quick start guide](/documentation/quick-start.md) for more information on how to set this up.

## **Locked Revisions in CI with lockfiles**
The CI in meta-arm will generate a kas "lock file" when it starts to ensure that all of the builds checkout the same revision of the various different layers that are used. If this isn't done then there's a chance that a layer will be modified upstream during the CI, which results in some builds failing and some builds passing.

This lock file is saved as an artefact of the update-repos job by the CI, and only generated if it doesn't already exist in the repository.  This can be used to force specific revisions of layers to be used instead of HEAD, which can be useful if upstream changes are causing problems in development.

The lockfile.yml can be downloaded manually, but there's a script in meta-arm to fetch the lock file for the latest successful build of the specified branch:

```
$ ./ci/download-lockfile.py --help
usage: download-lockfile.py [-h] server project refspec

positional arguments:
  server      GitLab server name
  project     meta-arm project name
  refspec     Branch/commit

$ ./ci/download-lockfile.py https://gitlab.com/jonmason00/meta-arm master
Fetched lockfile.yml
Commit this lockfile.yml to the top-level of the meta-arm repository and the CI will use it automatically.
```
# **Relevant Links for kas, CI, and testing**
<https://github.com/siemens/kas.git>

<https://wiki.yoctoproject.org/wiki/Oe-selftest>

<https://wiki.yoctoproject.org/wiki/Image_tests>

<https://wiki.yoctoproject.org/wiki/Ptest>

<https://wiki.yoctoproject.org/wiki/BSP_Test_Plan>
