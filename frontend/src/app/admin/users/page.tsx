"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "@/lib/api-client";
import { Navbar } from "@/components/shared/navbar";
import type { PageResponse } from "@/types/api";

interface UserItem {
  id: string;
  email: string;
  displayName: string;
  role: string;
  accountStatus: string;
  createdAt: string;
}

export default function AdminUsersPage() {
  const queryClient = useQueryClient();

  const { data, isLoading } = useQuery({
    queryKey: ["admin-users"],
    queryFn: () => api.get<PageResponse<UserItem>>("/users?size=50"),
  });

  const changeRole = useMutation({
    mutationFn: ({ userId, role }: { userId: string; role: string }) =>
      api.put(`/users/${userId}/role`, { role }),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ["admin-users"] }),
  });

  return (
    <div className="flex min-h-screen flex-col">
      <Navbar />
      <main className="flex-1 py-8">
        <div className="mx-auto max-w-7xl px-4">
          <h1 className="text-3xl font-bold text-white">User Management</h1>
          <p className="mt-1 text-muted-foreground">Manage user accounts and roles</p>

          {isLoading ? (
            <div className="mt-8 flex justify-center">
              <div className="h-8 w-8 animate-spin rounded-full border-2 border-primary border-t-transparent" />
            </div>
          ) : data && data.content.length > 0 ? (
            <div className="mt-8 overflow-hidden rounded-xl border border-white/10">
              <table className="w-full text-sm">
                <thead className="bg-white/5">
                  <tr className="text-left text-xs text-muted-foreground">
                    <th className="p-4">Name</th>
                    <th className="p-4">Email</th>
                    <th className="p-4">Role</th>
                    <th className="p-4">Status</th>
                    <th className="p-4">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {data.content.map((user) => (
                    <tr key={user.id} className="border-t border-white/5">
                      <td className="p-4 text-white">{user.displayName}</td>
                      <td className="p-4 text-muted-foreground">{user.email}</td>
                      <td className="p-4">
                        <span className={`rounded-full px-2 py-0.5 text-xs font-medium ${
                          user.role === "ADMIN" ? "bg-red-500/10 text-red-400" :
                          user.role === "INSTRUCTOR" ? "bg-blue-500/10 text-blue-400" :
                          "bg-green-500/10 text-green-400"
                        }`}>{user.role}</span>
                      </td>
                      <td className="p-4 text-muted-foreground">{user.accountStatus}</td>
                      <td className="p-4">
                        <select
                          defaultValue={user.role}
                          onChange={(e) => {
                            if (confirm(`Change ${user.displayName}'s role to ${e.target.value}?`)) {
                              changeRole.mutate({ userId: user.id, role: e.target.value });
                            }
                          }}
                          className="rounded-lg border border-white/10 bg-white/5 px-2 py-1 text-xs text-white"
                        >
                          <option value="STUDENT">Student</option>
                          <option value="INSTRUCTOR">Instructor</option>
                          <option value="ADMIN">Admin</option>
                        </select>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            <p className="mt-8 text-muted-foreground">No users found.</p>
          )}
        </div>
      </main>
    </div>
  );
}
