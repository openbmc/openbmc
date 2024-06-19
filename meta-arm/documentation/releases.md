# **meta-arm Releases and Branching**
## **Release and Branching background**
The Yocto Project releases twice a year (April and October): "stable" releases are made every six months and have a lifetime of seven months to allow for migration, while "long term support" (LTS) releases are picked every two years starting from Dunfell in April 2020. The standard practice for all Yocto Compatible layers is to create a "named" branch consistent with the code name of that release. For example, the “dunfell” release of the Yocto Project will have a branch named “dunfell” in the official git repository, and layers compatible with dunfell will have a branch named “dunfell”. Thus, a customer can easily organize a collection of appropriate layers to make a product.

In the Yocto Project, these named branches are “stable”, and only take bug fixes or security-critical upgrades. Active development occurs on the master branch. However, this methodology can be problematic if mimicked with the compatible layers. Companies, like Arm, may not wish to release a snapshot of the relevant “master” branches under active development, due to the amount of testing, fixing, and hardening necessary to make a product from a non-stable release. Also, changes to keep the master branch of a layer working with the upstream master branch of the Yocto Project may result in that branch no longer being compatible with named branches (e.g., it might not be possible to mix and match master and dunfell). So, a decision must be made on the branching policy of meta-arm.

## **Adding new Hardware or Software features**
There are many different ways to resolve this issue. After some discussion, the best solution for us is to allow new hardware enablement (and relevant software features) to be included in LTS named branches (not just bug fixes). This will allow for a more stable software platform for software to be developed, tested, and released. Also, the single branch allows for focused testing (limiting the amount of resources needed for CI/CD), lessens/eliminates code diverging on various branches, and lessens confusion on which branch to use. The risk of making this choice is a potentially non-stable branch which will require more frequent testing to lessen the risk, and not following the “stable” methodology of the core Yocto Project layers (though it is not uncommon for BSP layers to behave this way).

## **Process**
The process for patches intended on being integrated into only the master branch is the normal internal process of pushing for code review and CI, approval and integration into upstream meta-arm master branch. 
For patches intended on being included in an LTS named branch, the preferred process is to upstream via the master branch, rebase the patch (or series against the intended LTS branch) and send email with the release name in the subject line after the "PATCH" (e.g., "[PATCH dunfell] Add foo to bar").

If there is a time crunch and the preferred way above cannot be completed in time, upstreaming via the LTS branch can occur. This follows the normal process above but without the master integration step.  However, any patches upstreamed in this manner must be pushed to master in a timely fashion (after the time crunch). Nagging emails will be sent and managers will be involved as the time grows.

## **Testing**
See [continuous-integration-and-kas.md](/documentation/continuous-integration-and-kas.md) for information how the layer is tested and what tests are run. It is presumed that all code will be compiled as part of the CI process of the gerrit code review. Also, testing on virtual platforms and code conformity checks will be run when enabled in the process.

## **Branching strategy and releases**
Named branches for meta-arm will be released as close as possible to the release of the YP LTS release. Meta-arm named branches will be created from the meta-arm master branch.

To minimize the additional work of maintaining multiple branches it is assumed that there will only be two active development branches at any given time: master and the most recent Long Term Stable (LTS) as the named branch. All previous named LTS branches will be EOLed when a new LTS has been released. Any branches that are EOLed will still exist in the meta-arm, but bug fix patches will be accepted. Limited to no testing will occur on EOL’ed branches. Exceptions to this can be made, but must be sized appropriately and agreed to by the relevant parties.

Named branch release will coincide with Yocto Project releases. These non-LTS branches will be bug fix only and will be EOLed on the next release (similar to the YP branching behavior).

### **Branch transitions**
When YP is approaching release, meta-arm will attempt to stabilize master so that the releases can coincide. 
* T-6 weeks - Email is sent to meta-arm mailing list notifying of upcoming code freeze of features to meta-arm
* T-4 weeks - Code freeze to meta-arm. Only bug fixes are taken at this point.
* T-0 - Official upstream release occurs. With no outstanding critical bugs, a new named branch is created based on the current meta-arm master branch. Previous named branches are now frozen and will not accept new patches (but will continue to be present for reference and legacy usage).

## **Tagging**
### **Branch Tagging**
When each branch is released, a git tag with the Yocto Project version number will be added.  For example, `4.3`.  Also, this tag version number will be prepended with "yocto" in a duplicate tag (e.g., "yocto-4.3").

Conciding with the Yocto Project release schedule, every branch which has one or more changes added to it in the previous 6 months will get a minor versioned tag (e.g., "4.3.1" and "yocto-4.3.1").

### **BSP Release Tagging**
BSP releases for those boards supported in meta-arm-bsp maybe have an additional tag to denote their software releases.  The tag will consist of the board name (in all capital letters), year, and month.  For example, "CORSTONE1000-2023.11".
The release schedule for this is outside the standard Yocto Project release candence, but is generally encouraged to be as close to these releases as possible.  Similarily, it is recommended the BSP releases be based on the latest LTS branch.

# **Relevant Links**
<https://wiki.yoctoproject.org/wiki/Releases>
