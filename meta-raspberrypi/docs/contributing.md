# Contributing

## Mailing list

The main communication tool we use is a mailing list:
* <yocto@yoctoproject.org>
* <https://lists.yoctoproject.org/listinfo/yocto>

Feel free to ask any kind of questions but always prepend your email subject
with "[meta-raspberrypi]". This is because we use the 'yocto' mailing list and
not a perticular 'meta-raspberrypi' mailing list.

## Patches and pull requests

All the contributions should be compliant with the openembedded patch
guidelines: <http://www.openembedded.org/wiki/Commit_Patch_Message_Guidelines>

To contribute to this project you should send pull requests to the github mirror
(<https://github.com/agherzan/meta-raspberrypi>). **Additionally** you can send
the patches for review to the above specified mailing list.

When creating patches for the mailing list, please use something like:

    git format-patch -s --subject-prefix='meta-raspberrypi][PATCH' origin

When sending patches to the mailing list, please use something like:

    git send-email --to yocto@yoctoproject.org <generated patch>

## Github issues

In order to manage and trace the meta-raspberrypi issues, we use github issues:
<https://github.com/agherzan/meta-raspberrypi/issues>

If you push patches which have a github issue associated, please provide the
issue number in the commit log just before "Signed-off-by" line(s). Example line
for a bug:
`[Issue #13]`
