# **Yocto Project quick start for Arm system software developers**
If you want to read the The Yocto Project official quick start documentation, go to <https://docs.yoctoproject.org/brief-yoctoprojectqs/index.html>

If that looks like too much reading, then here is how to do it even faster!
# **Step 0: Install build deps and kas**
```
$ sudo apt install gawk wget git diffstat unzip texinfo gcc build-essential chrpath socat cpio python3 python3-pip python3-pexpect xz-utils debianutils iputils-ping python3-git python3-jinja2 libegl1-mesa libsdl1.2-dev python3-subunit mesa-common-dev zstd liblz4-tool file locales libacl1

$ pip install kas
```
OR, if you prefer to use a docker will all that stuff already installed:

```
$ sudo docker run -it --name kas-test --volume /mnt/yocto/:/builds/persist ghcr.io/siemens/kas/kas /bin/bash
```

> **_NOTE:_**
> the “--volume” is the directory where your persistent stuff (like downloads and build artifacts) will go to help speed up your builds and can be sharable amongst your builds/containers.  If you want to go completely clean-room, feel free to remove it
# **Step 1: clone meta-arm and build meta-arm**
```
$ git clone https://git.yoctoproject.org/meta-arm
$ cd meta-arm/
$ SSTATE_DIR=/builds/persist/sstate DL_DIR=/builds/persist/downloads kas build ci/fvp-base.yml:ci/testimage.yml
```
> **_NOTE:_**
> “ci/testimage.yml” will cause the build to run some basic system tests.  If you don’t care about verifying basic functionality, then remove it and it should be faster (a few less programs will be added to the system image and the 2-3mins that it takes to run the test will not happen).

> **_NOTE:_**
> You may wish to add the Yocto Project SSTATE Mirror (especially the first time) to speed up the build by downloading the build fragments (built by the Yocto Project autobuilder) from the internet.  This can be done by adding "ci/sstate-mirror.yml" in kas or adding the relevant lines to your local.conf.  Using the above example:

```
$ SSTATE_DIR=/builds/persist/sstate DL_DIR=/builds/persist/downloads kas build ci/fvp-base.yml:ci/sstate-mirror.yml
```

> **_NOTE:_**
> This only fetches the parts necessary for your build and may take several minutes depending on your internet connection speed.  Also, it only fetches what is available.  There may still be a need to build things depending on your configuration.

For more information on kas and various commands, please reference <https://kas.readthedocs.io/en/latest/>.

Depending on what software you are building, fvp-base might not be the machine you want to build for.
The following website provides an EXTREMELY rough way to tell what software is in what machines, and what versions are being run:
<https://gitlab.com/jonmason00/meta-arm/-/jobs/artifacts/master/file/update-report/index.html?job=pending-updates>

If, as an example, we’re wanting to develop trusted-firmware-a; then fvp-base will work for us. 

### **Okay, you are done!  VICTORY!**
### **Oh, you actually wanted to mess around with the system software source code?**
# **Step 2: use devtool to get your source**
Setup your environment via the (non-kas) Yocto Project tools

```
$ source poky/oe-init-build-env
```

Use devtool to checkout the version of software being used on the machine above (in the above example, this will be trusted-firmware-a for fvp-base).

```
$ devtool modify trusted-firmware-a
```

This will download the source, hopefully in git (depending on how the Yocto Project recipe was written), and should print a path at the end where the source code was checked out.  In the trusted-firmware-a example, I got:

> /builder/meta-arm/build/workspace/sources/trusted-firmware-a

Inside of that directory, you should see the relevant source code.  In this example, it is a standard git tree.  So, you can add remotes, checkout different SHAs, etc

Ok, so you are set with your changes and want to build them.

```
$ devtool build trusted-firmware-a
```

This should build the software in question, but it is not yet integrated into a system image.  To do that, run:

```
$ devtool build-image core-image-sato
```

The image should match the image being used on your machine above.  Most of them in meta-arm are set to core-image-sato.  

Also, if you used testimage above, it will run testimage now
### **Okay, you are done!  VICTORY!**
# **Step 3.  Testing your patches outside of devtool**
At this point I will assume you have a patch and want to add it to the base recipe.  Using the above example, in the devtool directory:
```
$ git format-patch -1
0001-example.patch
$ mv 0001-example.patch ~/meta-arm/meta-arm/recipes-bsp/trusted-firmware-a/files/
$ cd ~/meta-arm
$ devtool reset trusted-firmware-a
$ echo ‘SRC_URI:append = " file://0001-example.patch" >> meta-arm/recipes-bsp/trusted-firmware-a/trusted-firmware-a_2.10.3.bb
```

> **_NOTE:_**
> there is a space before the “file” and yes it matters very much

At this point, you can go back using kas and verify that the patch works in a clean-ish tree.

```
$ SSTATE_DIR=/builds/persist/sstate DL_DIR=/builds/persist/downloads kas build ci/fvp-base.yml:ci/testimage.yml
```

There is obviously much more that can be done and other ways to do similar things.

## **If there are issues or questions then please ask them on the #meta-arm irc channel on libera.chat**
