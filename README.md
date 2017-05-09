![Silverspoon](https://raw.githubusercontent.com/px3/px3.github.io/master/img/silverspoon-logo.png)

Project currently consists of the following:
* [Bulldog GPIO Library Camel Component](https://github.com/px3/silverspoon/tree/devel/camel-bulldog)
* [Temperature Sensor Camel Component](https://github.com/px3/silverspoon/tree/devel/camel-temperature)

We also provide maven archetypes demonstrating features of the above components:
* [Silverspoon LED Archetype](https://github.com/px3/silverspoon/tree/devel/silverspoon-archetypes/silverspoon-led)
* [Silverspoon Temperature Archetype](https://github.com/px3/silverspoon/tree/devel/silverspoon-archetypes/silverspoon-temperature)

_Note:_ Silverspoon is countinuously being uploaded to sonatype snapshot repository. In order to use the snapshot version add the following maven repository to your maven settings:

```xml
<repositories>
  <repository>
    <id>sonatype-public-repository</id>
    <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    <snapshots>
      <enabled>true</enabled>
    </snapshots>
  </repository>
</repositories>
```


[![Build Status](https://travis-ci.org/px3/silverspoon.svg?branch=master)](https://travis-ci.org/px3/silverspoon)
