package world.hello;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.java.html.BrwsrCtx;
import net.java.html.json.ComputedProperty;
import net.java.html.json.Function;
import net.java.html.json.Model;
import net.java.html.json.Models;
import net.java.html.json.OnPropertyChange;
import net.java.html.json.Property;
import world.hello.js.Dialogs;
import world.hello.js.Router;

/**
 * Model annotation generates class Data with one message property, boolean
 * property and read only words property
 */
@Model(className = "Data", targetId = "control", properties = {
    @Property(name = "message", type = String.class),
    @Property(name = "started", type = boolean.class)
})
final class DataModel {

    @Model(className = "ThreadData", targetId = "", properties = {
        @Property(name = "name", type = String.class),
        @Property(name = "currentTime", type = String.class),
        @Property(name = "testArray", type = MIFSFPTrunkVM.class, array = true)
    })
    public class ThreadModel {

    }

    private static Data ui;

    private static BrwsrCtx ctx;

    private static ThreadData test1;
    private static ThreadData test2;

    private static boolean isTest1BindLastTime = false;

    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        ui = new Data();
        Models.toRaw(ui);
        Router.registerBinding();
//        ui.applyBindings();
        Models.applyBindings(ui, "control");
        ctx = BrwsrCtx.findDefault(Data.class);
        test1 = new ThreadData();
        test2 = new ThreadData();
        for (int i = 0; i < 100; ++i) {
            test2.getTestArray().add(new MIFSFPTrunkVM());
            test1.getTestArray().add(new MIFSFPTrunkVM());
        }
        test1.setName("Test 1");
        test2.setName("Test 2");
        Models.applyBindings(test1, "thread4");
        Models.applyBindings(test2, "thread5");
    }

     // Test1, just update the value of view models in an array
    // Result: No memory leak
    @Function
    public static void updateViewModels(Data data) {
        updateViewModelArrayValues();
    }

    // Test2, remove all items in an array, and recreate new items.
    // Result: Webkit didn't release the memory for the cleared items in native code, the memory was increasing all the time.
    @Function
    public static void createViewModels(Data data) {
        //
        createViewModelInArrayEveryTwoSeconds();
    }

    // Test 3: Just create a bunch of view models, dont do the bind.
    // Result: No memory leak.
    @Function
    public static void createViewModelsNotBinding(Data data) {
        //
        create200EmptyViewModels();
    }
    

    // Test 4: rebind view model again and again.
    //This function will switch the bind of test1 and test2 to the same div element
    // To verify that if the rebind will make webkit allocate new memory in native code or not
    // The result shows that it is OK to rebind existing viewmodel to the dom, it won't cause memory leak.
    @Function
    public static void reBindAllTheTime(Data data) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    ctx.execute(new Runnable() {

                        @Override
                        public void run() {
                            if (isTest1BindLastTime) {
                                Models.applyBindings(test2, "rebind");
                            } else {
                                Models.applyBindings(test1, "rebind");
                            }
                            isTest1BindLastTime = !isTest1BindLastTime;
                        }
                    });
                }
            }
        };
        
        new Thread(task).start();
    }

    //Test 5, remove the dom node "thread4" by KnockoutJS api.
    // Result, it didn't clean up the javascript memory in webkit native code.
    @Function
    public static void cleanUp(Data data) {
        cleanUpNode("thread4");
    }
    
    
    private static void cleanUpNode(final String divId) {
        Dialogs.removeNode(divId);
    }

    private static void create200EmptyViewModels() {
        for (int i = 0; i < 200; ++i) {
            MIFSFPTrunkVM mifsfpTrunkVM = new MIFSFPTrunkVM();
        }
    }

    private static void updateViewModelArrayValues() {

        Runnable task = new Runnable() {

            @Override
            public void run() {

                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    test2.setCurrentTime(ZonedDateTime.now().toString());
                    for (MIFSFPTrunkVM item : test2.getTestArray()) {
                        item.setDeviceName(ZonedDateTime.now().toString());
                    }
                }
            }

        };

        new Thread(task).start();
    }

    // This method will start a new thread that re-create 100 sub view models  in the array
    // The result shows that it will cause memory leak as webkit didn't release previous objects 
    private static void createViewModelInArrayEveryTwoSeconds() {

        Runnable task = new Runnable() {

            @Override
            public void run() {

                final List<MIFSFPTrunkVM> newlist = new ArrayList<>();
                while (true) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(DataModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    newlist.clear();
                    for (int i = 0; i < 100; ++i) {
                        newlist.add(new MIFSFPTrunkVM());
                    }
                    ctx.execute(new Runnable() {

                        @Override
                        public void run() {
                            test1.getTestArray().clear(); //This clear will release the Java Objects but the webkit didn't release the corresponding memory in the native code
                            test1.getTestArray().addAll(newlist);
                        }
                    });
                    ctx.execute(new Runnable() {
                        @Override
                        public void run() {
                            test1.setCurrentTime(ZonedDateTime.now().toString());
                        }
                    });

                }
            }

        };

        new Thread(task).start();

    }

}
