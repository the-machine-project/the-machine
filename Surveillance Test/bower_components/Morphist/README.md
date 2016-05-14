Morphist
========

[![Dev Dependency Status](https://david-dm.org/MrSaints/Morphist/dev-status.svg?style=flat)](https://david-dm.org/MrSaints/Morphist#info=devDependencies)

A simple, high-performance and cross-browser [jQuery][jquery] slider / slideshow / carousel plugin for child objects powered by [Animate.css][animatecss].

It cycles through an element's [child] objects containing any content (i.e. images, text, etc) in quick succession and it is a great tool for displaying tweets from Twitter or items from other feeds.
It is a spin-off project of [Morphext](//github.com/MrSaints/Morphext) (a simple text rotator).

[Website / Demo][website]


Install
-------

Download from the [project page][downloads].

Install with [Bower][bower]: `bower install --save Morphist`


Usage
-----

1. Import the latest [Animate.css][animatecss] and [jQuery][jquery] library into your HTML.

2. Import `morphist.css` and include `morphist.min.js` in your HTML document.

3. Encapsulate your rotating objects (children, e.g. list item) in an element (parent, e.g. unordered list):

    ```html
    I am a...
    <ul id="js-rotating">
        <li>So Simple</li>
        <li>Very Doge</li>
        <li>Much Wow</li>
        <li>Such Cool</li>
    </ul>
    ...child object rotator.
    ```

4. Trigger the plugin by calling Morphist() on the element (parent) containing the rotating objects (children):

    ```js
    $("#js-rotating").Morphist();
    ```

A demo titled `index.html` is included in this repository. Open it to see the end-result.


Options
-------

Morphist exposes the following options to alter the behaviour of the plugin:

Option | Type | Default | Description
--- | --- | --- | ---
animateIn | `string` | `bounceIn` | The entrance animation type (In).
animateOut | `string` | `rollOut` | The exit animation type (Out). Refer to [Animate.css][animatecss] for a list of available animations.
speed | `int` | `2000` | The delay between the changing of each object in milliseconds.
complete | `object Function` | `null` | A callback that is executed after an item is animated in.

They may be used like so:

```js
$("#js-rotating").Morphist({
    animateIn: "fadeIn", // Overrides default "bounceIn"
    animateOut: "zoomOut", // Overrides default "rollOut"
    speed: 3000, // Overrides default 2000
    complete: function () {
        // Overrides default empty function
    }
});
```

The plugin relies heavily on [Animate.css][animatecss] for its [smooth, high performance animations](http://www.html5rocks.com/en/tutorials/speed/high-performance-animations/) to transition between each object. Thus, the default animation speed (different from the interval between each object as described above) may be altered via CSS:

```css
#yourElement, .yourClass {
    /* Overrides Animate.css 1s duration */
    -vendor-animation-duration: 3s;
}
```


"Issues"
----------

Refer to [Morphext's "Issues"](https://github.com/MrSaints/Morphext#issues).

Should you encounter any problems or require assistance with this plugin, simply open a GitHub issue in this repository or you may contact [me via Twitter][twitter].


Prerequisites
-------------

- [jQuery][jquery]
- [Animate.css][animatecss]


License
-------

Morphist is licensed under the MIT license [(http://ian.mit-license.org/)](http://ian.mit-license.org/).

  [website]: http://morphist.fyianlai.com/
  [twitter]: https://www.twitter.com/MrSaints
  [downloads]: https://github.com/MrSaints/Morphist/releases

  [bower]: http://bower.io/
  [jquery]: https://www.jquery.com/
  [animatecss]: //daneden.github.io/animate.css/
