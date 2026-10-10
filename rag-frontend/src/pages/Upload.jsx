import { useState, useRef, useEffect } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Upload as UploadIcon, FileText, ArrowLeft, CheckCircle, XCircle, Clock, LogOut, MessageSquare } from 'lucide-react'

function Upload() {
  const [dragActive, setDragActive] = useState(false)
  const [uploading, setUploading] = useState(false)
  const [documents, setDocuments] = useState([])
  const [error, setError] = useState('')
  const fileInputRef = useRef(null)
  const navigate = useNavigate()

  const user = JSON.parse(localStorage.getItem('user') || '{}')
  const token = localStorage.getItem('token')

  const fetchDocuments = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/documents/my-documents', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      const data = await response.json()
      setDocuments(data)
    } catch (err) {
      console.error('Failed to fetch documents:', err)
    }
  }

  useEffect(() => {
    fetchDocuments()
  }, [])

  const handleDrag = (e) => {
    e.preventDefault()
    e.stopPropagation()
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true)
    } else if (e.type === 'dragleave') {
      setDragActive(false)
    }
  }

  const handleDrop = (e) => {
    e.preventDefault()
    e.stopPropagation()
    setDragActive(false)

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      uploadFile(e.dataTransfer.files[0])
    }
  }

  const handleFileSelect = (e) => {
    if (e.target.files && e.target.files[0]) {
      uploadFile(e.target.files[0])
    }
  }

  const uploadFile = async (file) => {
    setError('')
    setUploading(true)

    const formData = new FormData()
    formData.append('file', file)

    try {
      const response = await fetch('http://localhost:8080/api/documents/upload', {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      })

      if (!response.ok) {
        throw new Error('Upload failed')
      }

      // Upload ke baad list refresh karo
      await fetchDocuments()
    } catch (err) {
      setError('Failed to upload file. Please try again.')
    } finally {
      setUploading(false)
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    navigate('/login')
  }

  const formatDate = (dateString) => {
    const date = new Date(dateString)
    return date.toLocaleDateString('en-US', { day: 'numeric', month: 'short', year: 'numeric' })
  }

  const getStatusIcon = (status) => {
    if (status === 'READY') return <CheckCircle size={18} className="text-green-400 flex-shrink-0" />
    if (status === 'FAILED') return <XCircle size={18} className="text-red-400 flex-shrink-0" />
    return <Clock size={18} className="text-yellow-400 flex-shrink-0 animate-pulse" />
  }

  return (
    <div className="min-h-screen bg-slate-900 flex">
      {/* Sidebar */}
      <div className="w-64 bg-slate-950 border-r border-slate-800 flex flex-col">
        <div className="p-4 border-b border-slate-800">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-indigo-600 rounded-lg flex items-center justify-center">
              <span className="text-sm font-bold text-white">AI</span>
            </div>
            <span className="text-white font-semibold">Knowledge Assistant</span>
          </div>
        </div>

        <div className="flex-1 p-4 space-y-1">
          <Link
            to="/chat"
            className="w-full flex items-center gap-2 text-slate-400 hover:text-white hover:bg-slate-800 px-3 py-2 rounded-lg transition-colors text-sm"
          >
            <MessageSquare size={16} />
            Chat
          </Link>
          <div className="w-full flex items-center gap-2 text-white bg-slate-800 px-3 py-2 rounded-lg text-sm">
            <FileText size={16} />
            Upload Documents
          </div>
        </div>

        <div className="p-4 border-t border-slate-800">
          <div className="flex items-center gap-2 text-slate-400 text-sm mb-3">
            <span>{user.name || 'User'}</span>
          </div>
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-2 text-slate-400 hover:text-red-400 px-3 py-2 rounded-lg transition-colors text-sm"
          >
            <LogOut size={16} />
            Logout
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 p-8 overflow-y-auto">
        <div className="max-w-2xl mx-auto">
          <div className="flex items-center gap-3 mb-8">
            <Link to="/chat" className="text-slate-400 hover:text-white transition-colors">
              <ArrowLeft size={20} />
            </Link>
            <h1 className="text-2xl font-bold text-white">Upload Documents</h1>
          </div>

          {/* Drop Zone */}
          <div
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
            className={`border-2 border-dashed rounded-2xl p-12 text-center transition-all cursor-pointer ${
              dragActive
                ? 'border-indigo-500 bg-indigo-500/10'
                : 'border-slate-700 bg-slate-800/30 hover:border-slate-600'
            }`}
            onClick={() => fileInputRef.current?.click()}
          >
            <input
              ref={fileInputRef}
              type="file"
              className="hidden"
              accept=".pdf,.doc,.docx,.txt"
              onChange={handleFileSelect}
            />
            <div className="w-16 h-16 bg-indigo-600/20 rounded-2xl flex items-center justify-center mx-auto mb-4">
              <UploadIcon className="text-indigo-400" size={28} />
            </div>
            <p className="text-white font-medium mb-1">
              {dragActive ? 'Drop your file here' : 'Drag & drop a file here'}
            </p>
            <p className="text-slate-400 text-sm mb-4">or click to browse</p>
            <p className="text-slate-500 text-xs">Supports PDF, DOC, DOCX, TXT</p>
          </div>

          {uploading && (
            <div className="mt-4 flex items-center gap-3 bg-slate-800/50 rounded-xl px-4 py-3">
              <div className="w-5 h-5 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin"></div>
              <span className="text-slate-300 text-sm">Uploading and processing...</span>
            </div>
          )}

          {error && (
            <p className="text-red-400 text-sm mt-4 text-center">{error}</p>
          )}

          {/* Documents List */}
          <div className="mt-8">
            <h2 className="text-slate-300 font-medium mb-3 text-sm">
              Your Documents ({documents.length})
            </h2>

            {documents.length === 0 ? (
              <p className="text-slate-500 text-sm text-center py-8">
                No documents uploaded yet
              </p>
            ) : (
              <div className="space-y-2">
                {documents.map((doc) => (
                  <div
                    key={doc.id}
                    className="flex items-center gap-3 bg-slate-800/50 border border-slate-700 rounded-xl px-4 py-3"
                  >
                    <FileText size={18} className="text-slate-400 flex-shrink-0" />
                    <div className="flex-1 min-w-0">
                      <p className="text-slate-200 text-sm truncate">{doc.filename}</p>
                      <p className="text-slate-500 text-xs">{formatDate(doc.uploadDate)}</p>
                    </div>
                    {getStatusIcon(doc.status)}
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  )
}

export default Upload