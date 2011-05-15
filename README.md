# GUI FTW! -- WTF?

GUI FTW! is a **declarative** and abstract GUI library. It's not tied
to any GUI toolkit but instead front-ends for each can be written
easily.

Features:

- Supports both **Swing** and **SWT** toolkits as front-ends.
- Allows user to write GUI code declaratively:
  - Declare your GUI structure using simple Clojure syntax. No XML!
  - Style it in a CSS fashion.
  - Put event handlers in stylesheets.

More in-depth info:
- [Wiki](https://github.com/santamon/GUIFTW/wiki),
- [Overview](https://github.com/santamon/GUIFTW/wiki/Overview) -- how
  it works, basic syntax, etc.,
- [Online Documentation](http://longstandingbug.com/GUIFTW) for
  functions and macros,
- [One Tutorial For All](https://github.com/santamon/GUIFTW/wiki/One-Tutorial-For-All) -- THE tutorial with Swing and SWT side-by-side.

# Status & Usage

It works. It's in *alpha* stage, that means some things will change
and it's a little rough on the edges.

Latest snapshot is available at
[https://github.com/santamon/GUIFTW/archives/master](Downloads)
section and in Lein/Cake through
[http://clojars.org/guiftw](Clojars). Just add to `:dependecies`:

```clj
[guiftw "0.2.0-SNAPSHOT"]
```

There are two analogical examples for Swing and SWT with lots of
comments. They're
[here](https://github.com/santamon/GUIFTW/blob/master/src/guiftw/examples/swing/basic.clj)
and
[here](https://github.com/santamon/GUIFTW/blob/master/src/guiftw/examples/swt/basic.clj). (They're
older version of code from the tutorial.)

# Disclaimer (Questions One Would Probably Ask)

1.  **Do I have to know Swing or SWT to use GUI FTW?**

    Yes. GUI FTW is responsible for generating redundant code you would
    write without it and providing more *declarative* approach to
    creating GUIs with Swing or SWT. Still, you have to know the
    toolkit you are using (classes names, properties, event listeners,
    etc.).

2.  **Can I run the same code against SWT and Swing?**

    Not at this point and it's not planned for the future. GUI FTW is
    portable across these toolkits because it's very abstract but your
    code created using it won't be (you'll be using specific classes,
    properties and event listeners to your toolkit of choice). Probably
    writing such a bridge could be done but its not in scope of this
    project (look at the other exciting features implemented and
    planned on the top of this README! ;) ).

3.  **Could support for framework X be added?**

    Probably yes. I know Swing and a little of SWT so GUI FTW was
    created keeping their similarities in mind. BUT: core of the
    framework is completely independent of one or the
    other. Technically writing support for another framework is
    writing one function that is responsible for creating object (like
    [https://github.com/santamon/GUIFTW/blob/master/src/guiftw/swing.clj](swing-create)
    or
    [https://github.com/santamon/GUIFTW/blob/master/src/guiftw/swt.clj](swt-create)).
