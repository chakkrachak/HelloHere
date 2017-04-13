//
//  ViewController.swift
//  HelloHereSwift
//
//  Created by Chakkra CHAK on 12/04/2017.
//  Copyright © 2017 Chakkra CHAK. All rights reserved.
//

import UIKit

class ViewController: UIViewController {

    @IBOutlet var mapView: NMAMapView!
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        mapView.useHighResolutionMap = true
        var coordinates: NMAGeoCoordinates
        coordinates = NMAGeoCoordinates(latitude: 49.258867, longitude: -123.008046)
        mapView.zoomLevel = 13.2
        mapView.set(geoCenter: coordinates, animation: NMAMapAnimation.linear)
        mapView.copyrightLogoPosition = NMALayoutPosition.bottomCenter
        addMapCircle()
    }
    
    func addMapCircle() {
        var mapCircle:NMAMapCircle?
        if mapCircle == nil {
            let coordinates: NMAGeoCoordinates =
                NMAGeoCoordinates(latitude: 49.258867, longitude: -123.008046)
            mapCircle = NMAMapCircle(coordinates: coordinates, radius: 50)
            mapView.add(mapCircle!)
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

