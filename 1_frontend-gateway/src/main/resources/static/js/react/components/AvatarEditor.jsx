// AvatarEditor.jsx
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import AvatarPreview from './AvatarPreview';

const AvatarEditor = ({ userId }) => {
    const [avatarConfig, setAvatarConfig] = useState({
        avatarType: 'CARTOON',
        faceShape: 'OVAL',
        skinTone: 'LIGHT',
        hairStyle: 'SHORT',
        hairColor: 'BROWN',
        eyeColor: 'BLUE',
    });

    const [previewUrl, setPreviewUrl] = useState('');
    const [loading, setLoading] = useState(false);

    // Load existing avatar if userId is provided
    useEffect(() => {
        if (userId) {
            fetchUserAvatar();
        }
    }, [userId]);

    const fetchUserAvatar = async () => {
        try {
            const response = await axios.get(`/api/avatars/user/${userId}`);
            setAvatarConfig(response.data.config);
            setPreviewUrl(response.data.avatarPreviewUrl);
        } catch (error) {
            console.error('Error fetching avatar:', error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setAvatarConfig(prev => ({
            ...prev,
            [name]: value
        }));
    };

    const generatePreview = async () => {
        setLoading(true);
        try {
            const response = await axios.post('/api/avatars/preview', { config: avatarConfig });
            setPreviewUrl(response.data.previewUrl);
        } catch (error) {
            console.error('Error generating preview:', error);
        } finally {
            setLoading(false);
        }
    };

    const saveAvatar = async () => {
        if (!userId) return;

        setLoading(true);
        try {
            const response = await axios.post(`/api/avatars/user/${userId}`, {
                config: avatarConfig
            });
            setPreviewUrl(response.data.avatarPreviewUrl);
            alert('Avatar saved successfully!');
        } catch (error) {
            console.error('Error saving avatar:', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="avatar-editor">
            <div className="editor-controls">
                <h2>Avatar Customization</h2>

                <div className="form-group">
                    <label>Avatar Type</label>
                    <select
                        name="avatarType"
                        value={avatarConfig.avatarType}
                        onChange={handleInputChange}
                    >
                        <option value="CARTOON">Cartoon</option>
                        <option value="SIMULATED">Simulated</option>
                        <option value="REALISTIC">Realistic</option>
                    </select>
                </div>

                <div className="form-group">
                    <label>Skin Tone</label>
                    <select
                        name="skinTone"
                        value={avatarConfig.skinTone}
                        onChange={handleInputChange}
                    >
                        <option value="LIGHT">Light</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="DARK">Dark</option>
                    </select>
                </div>

                <div className="form-group">
                    <label>Hair Style</label>
                    <select
                        name="hairStyle"
                        value={avatarConfig.hairStyle}
                        onChange={handleInputChange}
                    >
                        <option value="SHORT">Short</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="LONG">Long</option>
                    </select>
                </div>

                {/* Add more customization options */}

                <div className="action-buttons">
                    <button
                        onClick={generatePreview}
                        disabled={loading}
                    >
                        {loading ? 'Generating...' : 'Preview'}
                    </button>

                    {userId && (
                        <button
                            onClick={saveAvatar}
                            disabled={loading}
                        >
                            {loading ? 'Saving...' : 'Save Avatar'}
                        </button>
                    )}
                </div>
            </div>

            <div className="preview-container">
                {previewUrl ? (
                    <img src={previewUrl} alt="Avatar Preview" className="avatar-preview-image" />
                ) : (
                    <AvatarPreview avatarConfig={avatarConfig} />
                )}
            </div>
        </div>
    );
};

export default AvatarEditor;