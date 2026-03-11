# Contributing

## Mailing list

The main communication tool in use is the Yocto Project mailing list:

* <yocto@lists.yoctoproject.org>
* <https://lists.yoctoproject.org/g/yocto>

Feel free to ask any kind of questions but please always prepend your email
subject with `[meta-raspberrypi]` as this is the global *Yocto* mailing
list and not a dedicated *meta-raspberrypi* mailing list.

## Formatting patches

First and foremost, all of the contributions to the layer must be compliant
with the standard openembedded patch guidelines:

* <http://www.openembedded.org/wiki/Commit_Patch_Message_Guidelines>

In summary, your commit log messages should be formatted as follows:

    <layer-component>: <short log/statement of what needed to be changed>

    (Optional pointers to external resources, such as defect tracking)

    The intent of your change.

    (Optional: if it's not clear from above, how your change resolves
    the issues in the first part)

    Signed-off-by: Your Name <yourname@youremail.com>

The `<layer-component>` is the layer component name that your changes affect.
It is important that you choose it correctly. A simple guide for selecting a
a good component name is the following:

* For changes that affect *layer recipes*, please just use the **base names**
  of the affected recipes, separated by commas (`,`), as the component name.
  For example: use `omxplayer` instead of `omxplayer_git.bb`. If you are
  adding new recipe(s), just use the new recipe(s) base name(s). An example
  for changes to multiple recipes would be `userland,vc-graphics,wayland`.
* For changes that affect the *layer documentation*, please just use `docs`
  as the component name.
* For changes that affect *other files*, i.e. under the `conf` directory,
  please use the full path as the component name, e.g. `conf/layer.conf`.
* For changes that affect the *layer itself* and do not fall into any of
  the above cases, please use `meta-raspberrypi` as the component name.

A full example of a suitable commit log message is below:

    foobar: Adjusted the foo setting in bar

    When using foobar on systems with less than a gigabyte of RAM common
    usage patterns often result in an Out-of-memory condition causing
    slowdowns and unexpected application termination.

    Low-memory systems should continue to function without running into
    memory-starvation conditions with minimal cost to systems with more
    available memory.  High-memory systems will be less able to use the
    full extent of the system, a dynamically tunable option may be best,
    long-term.

    The foo setting in bar was decreased from X to X-50% in order to
    ensure we don't exhaust all system memory with foobar threads.

    Signed-off-by: Joe Developer <joe.developer@example.com>

A common issue during patch reviewing is commit log formatting, please review
the above formatting guidelines carefully before sending your patches.

## Sending patches

The preferred method to contribute to this project is to send pull
requests to the GitHub mirror of the layer:

* <https://github.com/agherzan/meta-raspberrypi>

**In addition**, you may send patches for review to the above specified
mailing list. In this case, when creating patches using `git` please make
sure to use the following formatting for the message subject:

    git format-patch -s --subject-prefix='meta-raspberrypi][PATCH' origin

Then, for sending patches to the mailing list, you may use this command:

    git send-email --to yocto@lists.yoctoproject.org <generated patch>

When patches are sent through the mailing list, the maintainer will include
them in a GitHub pull request that will take the patches through the CI
workflows. This process happens periodically.

## GitHub issues

In order to manage and track the layer issues more efficiently, the
GitHub issues facility is used by this project:

* <https://github.com/agherzan/meta-raspberrypi/issues>

If you submit patches that have a GitHub issue associated, please make sure to
use standard GitHub keywords, e.g. `closes`, `resolves` or `fixes`, before the
"Signed-off-by" tag to close the relevant issues automatically:

    foobar: Adjusted the foo setting in bar

    Fixes: #324

    Signed-off-by: Joe Developer <joe.developer@example.com>

More information on the available GitHub close keywords can be found here:

* <https://help.github.com/articles/closing-issues-using-keywords>

