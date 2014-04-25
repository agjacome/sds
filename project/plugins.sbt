resolvers += Resolver.sonatypeRepo("public")

resolvers += "Typesafe repository" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.2.2")

addSbtPlugin("com.typesafe.sbt"  % "sbt-git"    % "0.6.3")

addSbtPlugin("org.scalaxb" % "sbt-scalaxb" % "1.1.2")
