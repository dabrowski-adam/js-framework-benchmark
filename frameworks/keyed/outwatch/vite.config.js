import { defineConfig }       from "vite"
import scalaJSPlugin          from "@scala-js/vite-plugin-scalajs"
import injectHtmlVarsPlugin   from "./vite-plugins/inject-html-vars.js"
import rollupPluginSourcemaps from "rollup-plugin-sourcemaps"

export default defineConfig(
  ({
    command,
    mode,
    ssrBuild,
  }) => {
    return {
      base:      "/frameworks/keyed/outwatch/dist/",
      publicDir: "public",
      plugins:   [
        scalaJSPlugin({
          cwd:       ".",        // Path to build.sbt
          projectID: "frontend", // Scala.js project name in build.sbt
        }),
        injectHtmlVarsPlugin({
          SCRIPT_URL: "./index.js",
        }),
      ],
      build: {
        outDir:        "dist",
        assetsDir:     "assets",  // Path relative to outDir
        cssCodeSplit:  false,     // Load all CSS upfront
        rollupOptions: { plugins: [rollupPluginSourcemaps()] },
        minify:        "terser",
        sourcemap:     true,
      },
      server: {
        port:       3000,
        strictPort: true,
        logLevel:   "debug",
      }
    }
  }
)