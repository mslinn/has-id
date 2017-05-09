// sbt-site settings
enablePlugins(SiteScaladocPlugin)
siteSourceDirectory := target.value / "api"
publishSite

// sbt-ghpages settings
enablePlugins(GhpagesPlugin)
git.remoteRepo := s"git@github.com:mslinn/${ name.value }.git"

// bintray settings
bintrayOrganization := Some("micronautics")
bintrayRepository := "scala"
bintrayVcsUrl := Some(s"git@github.com:mslinn/${ name.value }.git")
