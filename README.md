# shoebill-dependency-manager

[![Build Status](http://ci.gtaun.net/app/rest/builds/buildType:(id:Shoebill_DependencyManager_Deploy)/statusIcon)](http://ci.gtaun.net/project.html?projectId=Shoebill_DependencyManager)

# What is the shoebill-dependency-manager?

The shoebill-dependency-manager is taking care of the available updates, to provide you the best experience.
With every start, the shoebill-dependency-manager will check for any new updates, and download them (except internal updates).
If you don't want it to check everytime, you can set offlineMode in resources.yml to true.
You can also change your customRepoPath for maven in the shoebill.yml and the dependency-manager will take care of it.

# How to use it?

There is no need to manualy use it. Shoebill will take care of all updates, you will just need to change offlineMode to true or false,
depending on your needs.
