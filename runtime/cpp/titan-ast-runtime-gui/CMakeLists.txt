cmake_minimum_required(VERSION 3.21)
project(titan_ast_runtime_gui)

set(CMAKE_CXX_STANDARD 14)
set(CMAKE_AUTOMOC ON)
set(CMAKE_AUTORCC ON)
set(CMAKE_AUTOUIC ON)

set(CMAKE_PREFIX_PATH "D:/Qt/Qt5.9.0/5.9/mingw53_32/lib/cmake")

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -fno-exceptions")
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -pthread")

#windows build lib(dll)
#add_definitions(-DWIN)
#add_definitions(-DBUILDING_DLL)

#windows build exe

#windows use lib
add_definitions(-DWIN)

#linux build lib
#add_definitions(-DBUILDING_DLL)

#linux build exe

# 指定头文件搜索路径
include_directories(${PROJECT_SOURCE_DIR}/lib)
# 指定lib目录
link_directories(${PROJECT_SOURCE_DIR}/lib)

find_package(Qt5 COMPONENTS
        Core
        Gui
        Widgets
        REQUIRED)

add_executable(titan_ast_runtime_gui main.cpp
        gui/GuiApi.h gui/GuiApi.cpp gui/MainWindow.h gui/MainWindow.cpp
        gui/StringTree.h gui/StringTree.cpp
        gui/StringTreeGraphicsItem.h gui/StringTreeGraphicsItem.cpp
        gui/AstGuiOutputer.h gui/AstGuiOutputer.cpp
        )

target_link_libraries(titan_ast_runtime_gui
        Qt5::Core
        Qt5::Gui
        Qt5::Widgets
        libtitan-ast-runtime-lib.dll
        )

add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
        COMMAND ${CMAKE_COMMAND} -E copy_directory
        "${PROJECT_SOURCE_DIR}/lib"
        "$<TARGET_FILE_DIR:${PROJECT_NAME}>")

if (WIN32)
    set(DEBUG_SUFFIX)
    if (CMAKE_BUILD_TYPE MATCHES "Debug")
        set(DEBUG_SUFFIX "d")
    endif ()
    set(QT_INSTALL_PATH "${CMAKE_PREFIX_PATH}")
    if (NOT EXISTS "${QT_INSTALL_PATH}/bin")
        set(QT_INSTALL_PATH "${QT_INSTALL_PATH}/..")
        if (NOT EXISTS "${QT_INSTALL_PATH}/bin")
            set(QT_INSTALL_PATH "${QT_INSTALL_PATH}/..")
        endif ()
    endif ()
    if (EXISTS "${QT_INSTALL_PATH}/plugins/platforms/qwindows${DEBUG_SUFFIX}.dll")
        add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
                COMMAND ${CMAKE_COMMAND} -E make_directory
                "$<TARGET_FILE_DIR:${PROJECT_NAME}>/plugins/platforms/")
        add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
                COMMAND ${CMAKE_COMMAND} -E copy
                "${QT_INSTALL_PATH}/plugins/platforms/qwindows${DEBUG_SUFFIX}.dll"
                "$<TARGET_FILE_DIR:${PROJECT_NAME}>/plugins/platforms/")
    endif ()
    foreach (QT_LIB Core Gui Widgets)
        add_custom_command(TARGET ${PROJECT_NAME} POST_BUILD
                COMMAND ${CMAKE_COMMAND} -E copy
                "${QT_INSTALL_PATH}/bin/Qt5${QT_LIB}${DEBUG_SUFFIX}.dll"
                "$<TARGET_FILE_DIR:${PROJECT_NAME}>")
    endforeach (QT_LIB)
endif ()
