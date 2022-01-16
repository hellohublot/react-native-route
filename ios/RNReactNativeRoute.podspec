
Pod::Spec.new do |s|
  s.name         = "RNReactNativeRoute"
  s.version      = "1.0.0"
  s.summary      = "RNReactNativeRoute"
  s.description  = <<-DESC
                  RNReactNativeRoute
                   DESC
  s.homepage     = "https://github.com/hellohublot"
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/author/RNReactNativeRoute.git", :tag => "master" }
  s.source_files  = "Classes/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

  