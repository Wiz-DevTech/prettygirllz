// AvatarPreview.jsx
import React, { useEffect, useRef } from 'react';
import * as THREE from 'three';

const AvatarPreview = ({ avatarConfig }) => {
    const mountRef = useRef(null);

    useEffect(() => {
        if (!mountRef.current) return;

        // Basic Three.js setup
        const scene = new THREE.Scene();
        const camera = new THREE.PerspectiveCamera(75, 1, 0.1, 1000);
        const renderer = new THREE.WebGLRenderer({ antialias: true });

        renderer.setSize(300, 300);
        renderer.setClearColor(0xf0f0f0);
        mountRef.current.appendChild(renderer.domElement);

        // Map avatar config to 3D representation
        const getSkinColor = () => {
            switch(avatarConfig.skinTone) {
                case 'DARK': return 0x8d5524;
                case 'MEDIUM': return 0xc68642;
                default: return 0xffdbac;
            }
        };

        // Create head
        const headGeometry = new THREE.SphereGeometry(5, 32, 32);
        const headMaterial = new THREE.MeshPhongMaterial({
            color: getSkinColor(),
            shininess: 30
        });
        const head = new THREE.Mesh(headGeometry, headMaterial);
        scene.add(head);

        // Add hair based on hairStyle
        if (avatarConfig.hairStyle) {
            const hairColor = avatarConfig.hairColor === 'BROWN' ? 0x3b2417 : 0x1a1a1a;
            const hairMaterial = new THREE.MeshPhongMaterial({ color: hairColor });

            let hairGeometry;

            switch(avatarConfig.hairStyle) {
                case 'LONG':
                    hairGeometry = new THREE.CylinderGeometry(5.1, 4, 8, 32);
                    break;
                case 'MEDIUM':
                    hairGeometry = new THREE.CylinderGeometry(5.1, 5, 4, 32);
                    break;
                default: // SHORT
                    hairGeometry = new THREE.SphereGeometry(5.1, 32, 16, 0, Math.PI * 2, 0, Math.PI / 2);
            }

            const hair = new THREE.Mesh(hairGeometry, hairMaterial);
            hair.position.y = avatarConfig.hairStyle === 'LONG' ? 2 : 0;
            head.add(hair);
        }

        // Add lighting
        const ambientLight = new THREE.AmbientLight(0xffffff, 0.5);
        scene.add(ambientLight);

        const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8);
        directionalLight.position.set(1, 1, 1);
        scene.add(directionalLight);

        camera.position.z = 15;

        // Animation loop
        const animate = () => {
            requestAnimationFrame(animate);
            head.rotation.y += 0.01;
            renderer.render(scene, camera);
        };

        animate();

        // Cleanup
        return () => {
            mountRef.current?.removeChild(renderer.domElement);
        };
    }, [avatarConfig]);

    return <div ref={mountRef} className="avatar-preview-container" />;
};

export default AvatarPreview;