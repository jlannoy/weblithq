package io.weblith.core;

import io.quarkus.runtime.annotations.Recorder;

// WIP
@Recorder
public class StaticAssetsHandlerRecorder {

    //    public Handler<RoutingContext> handler(String path) {
    //
    //        Handler<RoutingContext> handler = new ThreadLocalHandler(new Supplier<Handler<RoutingContext>>() {
    //            @Override
    //            public Handler<RoutingContext> get() {
    //                return StaticHandler.create().setAllowRootFileSystemAccess(true)
    //                        .setWebRoot(path)
    //                        .setDefaultContentEncoding("UTF-8");
    //            }
    //        });
    //
    //        return new Handler<RoutingContext>() {
    //            @Override
    //            public void handle(RoutingContext event) {
    //                handler.handle(event);
    //            }
    //        };
    //    }

}