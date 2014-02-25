package org.cube.bender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//                      .-.
//                     (   )
//                      '-'
//                      J L
//                      | |
//                     J   L
//                     |   |
//                    J     L
//                  .-'.___.'-.
//                 /___________\
//            _.-""'           `bmw._
//          .'                       `.
//        J                            `.
//       F                               L
//      J                                 J
//     J                                  `
//     |                                   L
//     |                                   |
//     |                                   |
//     |                                   J
//     |                                    L
//     |                                    |
//     |             ,.___          ___....--._
//     |           ,'     `""""""""'           `-._
//     |          J           _____________________`-.
//     |         F         .-'   `-88888-'    `Y8888b.`.
//     |         |       .'         `P'         `88888b \
//     |         |      J       #     L      #    q8888b L
//     |         |      |             |           )8888D )
//     |         J      \             J           d8888P P
//     |          L      `.         .b.         ,88888P /
//     |           `.      `-.___,o88888o.___,o88888P'.'
//     |             `-.__________________________..-'
//     |                                    |
//     |         .-----.........____________J
//     |       .' |       |      |       |
//     |      J---|-----..|...___|_______|
//     |      |   |       |      |       |
//     |      Y---|-----..|...___|_______|
//     |       `. |       |      |       |
//     |         `'-------:....__|______.J
//     |                                  |
//      L___                              |
//          """----...______________....--' 
//
/**
 * 
 * @author user
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PARAMETER})
public @interface Bender {
	
	String value();

}
