set(CMAKE_CXX_STANDARD 14)
if (CMAKE_HOST_APPLE)
    set(MACOS 1)
endif()
if(MACOS)
    include_directories("/opt/homebrew/Cellar/webp/1.3.0/include")
    include_directories("/opt/homebrew/Cellar/libpng/1.6.39/include/")
    link_directories("/opt/homebrew/Cellar/webp/1.3.0/lib/")
    link_directories("/opt/homebrew/Cellar/libpng/1.6.39/lib/")
endif()

add_library(webp2png SHARED webptopng.h)
target_sources(webp2png PRIVATE webp2png)
target_link_libraries(webp2png -lwebp -lpng)